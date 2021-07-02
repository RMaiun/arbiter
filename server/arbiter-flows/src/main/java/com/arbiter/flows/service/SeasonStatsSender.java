package com.arbiter.flows.service;

import static com.arbiter.core.utils.DateUtils.notLateToSend;
import static com.arbiter.core.utils.SeasonUtils.currentSeason;
import static com.arbiter.core.utils.SeasonUtils.firstBeforeSecond;
import static com.arbiter.flows.utils.Constants.LINE_SEPARATOR;
import static com.arbiter.flows.utils.Constants.PREFIX;
import static com.arbiter.flows.utils.Constants.SUFFIX;
import static com.arbiter.flows.utils.IdGenerator.msgId;
import static java.util.Arrays.asList;
import static java.util.Objects.nonNull;

import com.arbiter.core.config.AppProperties;
import com.arbiter.core.dto.player.FoundPlayers;
import com.arbiter.core.dto.player.PlayerDto;
import com.arbiter.core.dto.player.PlayerRank;
import com.arbiter.core.dto.round.FullRound;
import com.arbiter.core.dto.stats.PlayerStats;
import com.arbiter.core.dto.stats.SeasonShortStats;
import com.arbiter.core.service.PlayerService;
import com.arbiter.core.service.RoundsService;
import com.arbiter.core.service.SeasonService;
import com.arbiter.core.service.StatisticsService;
import com.arbiter.flows.dto.OutputMessage;
import com.arbiter.flows.dto.SeasonNotificationData;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class SeasonStatsSender {

  public static final Logger log = LogManager.getLogger(SeasonStatsSender.class);

  private final AppProperties appProps;
  private final PlayerService playerService;
  private final RabbitSender rabbitSender;
  private final StatisticsService statisticsService;
  private final RoundsService roundsService;
  private final SeasonService seasonService;

  public SeasonStatsSender(AppProperties appProps, PlayerService playerService, RabbitSender rabbitSender, StatisticsService statisticsService, RoundsService roundsService,
      SeasonService seasonService) {
    this.appProps = appProps;
    this.playerService = playerService;
    this.rabbitSender = rabbitSender;
    this.statisticsService = statisticsService;
    this.roundsService = roundsService;
    this.seasonService = seasonService;
  }

  public void sendFinalSeasonStats() {
    if (appProps.notificationsEnabled) {
      log.info("Starting Final Season Stats Reports generation with notificationEnabled {}", appProps.notificationsEnabled);
      var date = ZonedDateTime.now(ZoneId.of(appProps.reportTimezone));
      log.info("Check that {} equals to last day of current season where time = 20:00", date);
      var snd = shouldSend(date);
      if (snd.readyToBeProcessed()) {
        log.info("Final Season Stats Reports generation criteria were passed successfully for season {}", snd.season());
        var ranks = findPlayersWithRanks(snd.season());
        log.info("{} users will receive final stats notification", ranks.size());
        ranks.forEach(pr -> sendNotificationForPlayer(pr, snd.season()));
        seasonService.ackSendFinalNotifications();
      }
    }
  }

  private SeasonNotificationData shouldSend(ZonedDateTime currentDateTime) {
    var snd = seasonService.findSeasonWithoutNotifications()
        .map(s -> {
          var isReady = firstBeforeSecond(s.getName(), currentSeason());
          var isNotLate = notLateToSend(currentDateTime);
          return new SeasonNotificationData(s.getName(), isReady && isNotLate);
        })
        .orElse(new SeasonNotificationData(null, false));
    log.info("Current date {} criteria for sending notifications", snd.readyToBeProcessed() ? "passed" : "didn't pass");
    return snd;
  }

  private List<PlayerRank> findPlayersWithRanks(String season) {
    var stats = statisticsService.seasonShortInfoStatistics(season);
    var players = playerService.findAllPlayers(true);
    var rounds = roundsService.findAllRounds(season);
    return preparePlayerRanks(stats, players, rounds);
  }

  private List<PlayerRank> preparePlayerRanks(SeasonShortStats stats, FoundPlayers allPlayers, List<FullRound> rounds) {
    List<String> participatedSurnames = rounds.stream()
        .flatMap(fr -> Stream.of(fr.winner1(), fr.winner2(), fr.loser1(), fr.loser2()))
        .distinct()
        .collect(Collectors.toList());

    Map<String, PlayerDto> allPlayersBySurname = allPlayers.players().stream()
        .collect(Collectors.toMap(PlayerDto::surname, Function.identity()));

    List<String> playersWithRating = stats.playersRating().stream()
        .map(PlayerStats::surname)
        .collect(Collectors.toList());

    Map<String, PlayerStats> statsPerPlayer = stats.playersRating().stream()
        .collect(Collectors.toMap(PlayerStats::surname, Function.identity()));

    return participatedSurnames.stream()
        .map(allPlayersBySurname::get)
        .filter(p -> p.notificationsEnabled() && nonNull(p.tid()))
        .map(p -> {
          Optional<PlayerStats> playerStats = Optional.ofNullable(statsPerPlayer.get(p.surname()));
          String score = playerStats.map(PlayerStats::score).orElse("");
          int gamesPlayed = (int) rounds.stream()
              .filter(r -> asList(r.winner1(), r.winner2(), r.loser1(), r.loser2()).contains(p.surname()))
              .count();
          int rank = playersWithRating.indexOf(p.surname());
          return new PlayerRank(p.surname(), p.tid(), rank + 1, score, gamesPlayed, rounds.size(), participatedSurnames.size());
        })
        .collect(Collectors.toList());
  }

  private void sendNotificationForPlayer(PlayerRank playerRank, String season) {
    StringBuilder builder = messageBuilder(playerRank, season);
    String msg = playerRank.rank() > 0
        ? messageForPlayerWithDefinedRating(builder, playerRank)
        : messageForPlayerWithoutRating(builder, playerRank);
    log.info(OutputMessage.ok(playerRank.tid(), msgId(), msg));
    rabbitSender.send(OutputMessage.ok(playerRank.tid(), msgId(), msg));
  }

  private StringBuilder messageBuilder(PlayerRank rank, String season) {
    return new StringBuilder()
        .append(PREFIX)
        .append(String.format("Season %s is successfully closed.", season))
        .append(LINE_SEPARATOR)
        .append(String.format("%d players played %d games in total.", rank.allPlayers(), rank.allGames()))
        .append(LINE_SEPARATOR);
  }

  private String messageForPlayerWithDefinedRating(StringBuilder builder, PlayerRank rank) {
    String winRate = rank.score();
    return builder
        .append("Your achievements:")
        .append(LINE_SEPARATOR)
        .append(String.format("- #%d in rating", rank.rank()))
        .append(LINE_SEPARATOR)
        .append(String.format("- win rate %s%%", winRate))
        .append(LINE_SEPARATOR)
        .append(String.format("- games played: %d", rank.gamesPlayed()))
        .append(LINE_SEPARATOR)
        .append("\uD83D\uDC4D\uD83D\uDC4D\uD83D\uDC4D")
        .append(SUFFIX)
        .toString();
  }

  private String messageForPlayerWithoutRating(StringBuilder builder, PlayerRank rank) {
    return builder
        .append(String.format("You've played %d games in this season.", rank.gamesPlayed()))
        .append(LINE_SEPARATOR)
        .append(String.format("Unfortunately you must play %d games", appProps.expectedGames))
        .append(LINE_SEPARATOR)
        .append("to be included into rating.")
        .append(LINE_SEPARATOR)
        .append("Hope that in next season")
        .append(LINE_SEPARATOR)
        .append("you will reach our game limit")
        .append(LINE_SEPARATOR)
        .append("and will show us your best.")
        .append(LINE_SEPARATOR)
        .append("⭐⭐⭐")
        .append(SUFFIX)
        .toString();
  }
}
