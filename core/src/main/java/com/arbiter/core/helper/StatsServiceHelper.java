package com.arbiter.core.helper;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import com.arbiter.core.config.AppProperties;
import com.arbiter.core.dto.AlgorithmType;
import com.arbiter.core.dto.stats.PlayerStats;
import com.arbiter.core.dto.stats.RatingWithGames;
import com.arbiter.core.dto.stats.SeasonShortStats;
import com.arbiter.core.dto.stats.SeasonStatsRows;
import com.arbiter.core.dto.stats.StatsCalcData;
import com.arbiter.core.dto.stats.Streak;
import com.arbiter.core.dto.round.FullRound;
import com.arbiter.core.utils.DateUtils;
import com.arbiter.core.utils.SeasonUtils;
import io.vavr.Tuple4;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

@Service
public class StatsServiceHelper {

  private final AppProperties appProps;

  public StatsServiceHelper(AppProperties appProps) {
    this.appProps = appProps;
  }

  public SeasonShortStats prepareSeasonShortStats(String seasonName, List<FullRound> rounds, AlgorithmType algorithmType) {
    Pair<LocalDate, LocalDate> seasonGate = SeasonUtils.seasonGate(seasonName);
    LocalDate now = LocalDate.now();
    var daysToSeasonEnd = now.compareTo(seasonGate.getRight()) > 0
        ? 0
        : seasonGate.getRight().getDayOfYear() - now.getDayOfYear();
    List<PlayerStats> topPlayers = AlgorithmType.PERCENTAGE == algorithmType
        ? percentagePlayersStats(rounds)
        : pointsPlayersStats(rounds);

    Pair<Optional<Streak>, Optional<Streak>> optionalOptionalPair = calculateStreaks(rounds);
    var best = optionalOptionalPair.getLeft().orElse(null);
    var worst = optionalOptionalPair.getRight().orElse(null);

    return new SeasonShortStats(seasonName, topPlayers, rounds.size(), daysToSeasonEnd, best, worst);
  }

  private List<PlayerStats> percentagePlayersStats(List<FullRound> rounds) {
    Map<String, Integer> allRounds = prepareAcceptedPlayersForShortStats(aggregatePlayersWithGames(rounds), true);
    Map<String, BigDecimal> winRounds = new HashMap<>();
    rounds.forEach(r -> {
      fillWinMap(r.winner1(), winRounds);
      fillWinMap(r.winner2(), winRounds);
    });
    return allRounds.entrySet().stream()
        .map(e -> new PlayerStats(e.getKey(), preparePercentageStats(e, winRounds), e.getValue()))
        .sorted(Comparator.comparing(ps -> new BigDecimal(ps.score()), Comparator.reverseOrder()))
        .collect(toList());
  }

  private String preparePercentageStats(Entry<String, Integer> entry, Map<String, BigDecimal> winRounds) {
    BigDecimal foundWins = winRounds.getOrDefault(entry.getKey(), BigDecimal.ZERO);
    BigDecimal result = foundWins.divide(BigDecimal.valueOf(entry.getValue()), new MathContext(3, RoundingMode.HALF_EVEN));

    BigDecimal roundedResult = result.setScale(3, RoundingMode.HALF_EVEN);
    return roundedResult.toString();
  }

  private void fillWinMap(String surname, Map<String, BigDecimal> winRounds) {
    BigDecimal winFound = winRounds.getOrDefault(surname, BigDecimal.ZERO);
    winRounds.put(surname, winFound.add(BigDecimal.ONE));
  }

  private List<PlayerStats> pointsPlayersStats(List<FullRound> rounds) {
    return calculatePointsForPlayers(rounds, true)
        .stream()
        .sorted(Comparator.comparing(RatingWithGames::rating, Comparator.reverseOrder()))
        .map(rwg -> new PlayerStats(rwg.player(), String.valueOf(rwg.rating()), rwg.games()))
        .collect(toList());
  }

  private Pair<Optional<Streak>, Optional<Streak>> calculateStreaks(List<FullRound> rounds) {
    Map<String, Tuple4<Integer, Integer, Integer, Integer>> results = rounds.stream()
        .flatMap(r -> Stream.of(r.winner1(), r.winner2(), r.loser1(), r.loser2()))
        .distinct()
        .collect(Collectors.toMap(x -> x, x -> new Tuple4<>(0, 0, 0, 0)));

    rounds.forEach(r -> {
      checkStreak(results, r.winner1(), 1);
      checkStreak(results, r.winner2(), 1);
      checkStreak(results, r.loser1(), -1);
      checkStreak(results, r.loser2(), -1);
    });

    Optional<Streak> best = results.entrySet().stream()
        .map(e -> new Streak(e.getKey(), e.getValue()._2))
        .max(Comparator.comparing(Streak::games));

    Optional<Streak> worst = results.entrySet().stream()
        .map(e -> new Streak(e.getKey(), e.getValue()._4))
        .max(Comparator.comparing(Streak::games));
    return Pair.of(best, worst);
  }

  private void checkStreak(Map<String, Tuple4<Integer, Integer, Integer, Integer>> results,
      String surname, int score) {
    Tuple4<Integer, Integer, Integer, Integer> found = results.get(surname);
    if (score > 0) {
      int currentWin = found._1 + 1;
      int maxWin = currentWin > found._2 ? currentWin : found._2;
      results.put(surname, new Tuple4<>(currentWin, maxWin, 0, found._4));
    } else {
      int currentLose = found._3 + 1;
      int maxLose = currentLose > found._4 ? currentLose : found._4;
      results.put(surname, new Tuple4<>(0, found._2, currentLose, maxLose));
    }
  }

  public SeasonStatsRows prepareSeasonStatsTable(List<FullRound> rounds) {
    List<RatingWithGames> playerStats = calculatePointsForPlayers(rounds, false);

    List<String> headers = playerStats.stream()
        .sorted(Comparator.comparing(RatingWithGames::player))
        .map(RatingWithGames::player)
        .collect(toList());

    List<Integer> totals = playerStats
        .stream()
        .sorted(Comparator.comparing(RatingWithGames::player))
        .map(RatingWithGames::rating)
        .collect(toList());

    List<List<String>> games = rounds.stream()
        .map(r -> transformRoundIntoRow(r, headers))
        .collect(toList());

    List<String> createdDates = rounds.stream()
        .map(FullRound::created)
        .map(DateUtils::formatDateWithHour)
        .collect(toList());

    return new SeasonStatsRows(headers, totals, games, createdDates, rounds.size());
  }

  private List<RatingWithGames> calculatePointsForPlayers(List<FullRound> rounds, boolean shortStats) {
    List<StatsCalcData> roundData = aggregatePlayersWithGames(rounds);
    Map<String, Integer> acceptedPlayers = prepareAcceptedPlayersForShortStats(roundData, shortStats);

    Map<String, List<StatsCalcData>> dataByPlayer = roundData.stream()
        .filter(t -> acceptedPlayers.containsKey(t.player()))
        .collect(groupingBy(StatsCalcData::player));

    return prepareRatingWithGames(dataByPlayer);
  }

  private List<StatsCalcData> aggregatePlayersWithGames(List<FullRound> rounds) {
    return rounds.stream()
        .flatMap(r -> Stream.of(
            new StatsCalcData(r.winner1(), winPoints(r.shutout()), 1),
            new StatsCalcData(r.winner2(), winPoints(r.shutout()), 1),
            new StatsCalcData(r.loser1(), losePoints(r.shutout()), 1),
            new StatsCalcData(r.loser2(), losePoints(r.shutout()), 1)))
        .collect(toList());
  }

  private Map<String, Integer> prepareAcceptedPlayersForShortStats(List<StatsCalcData> roundData, boolean filterByGames) {
    Stream<Entry<String, Integer>> stream = roundData.stream()
        .collect(groupingBy(StatsCalcData::player, mapping(StatsCalcData::qty, reducing(0, Integer::sum))))
        .entrySet().stream();
    if (filterByGames) {
      return stream.filter(e -> e.getValue() >= appProps.expectedGames)
          .collect(toMap(Entry::getKey, Entry::getValue));
    } else {
      return stream.collect(toMap(Entry::getKey, Entry::getValue));
    }
  }

  private List<RatingWithGames> prepareRatingWithGames(Map<String, List<StatsCalcData>> dataByPlayer) {
    return dataByPlayer.entrySet().stream()
        .map(this::ratingWithGames)
        .collect(toList());
  }

  private RatingWithGames ratingWithGames(Entry<String, List<StatsCalcData>> e) {
    int points = e.getValue().stream().map(StatsCalcData::points).reduce(1000, Integer::sum);
    int games = e.getValue().size();
    return new RatingWithGames(e.getKey(), points, games);
  }

  private List<String> transformRoundIntoRow(FullRound r, List<String> headers) {
    List<String> row = IntStream.range(0, headers.size()).mapToObj(x -> "").collect(toList());
    String winPoints = String.valueOf(winPoints(r.shutout()));
    String losePoints = String.valueOf(losePoints(r.shutout()));
    row.set(headers.indexOf(r.winner1()), winPoints);
    row.set(headers.indexOf(r.winner2()), winPoints);
    row.set(headers.indexOf(r.loser1()), losePoints);
    row.set(headers.indexOf(r.loser2()), losePoints);
    return row;
  }

  private int winPoints(boolean shutout) {
    return calculatePoints(true, shutout);
  }

  private int losePoints(boolean shutout) {
    return calculatePoints(false, shutout);
  }

  private int calculatePoints(boolean win, boolean shutout) {
    if (win && shutout) {
      return appProps.winShutoutPoints;
    } else if (win) {
      return appProps.winPoints;
    } else if (shutout) {
      return appProps.loseShutoutPoints;
    } else {
      return appProps.losePoints;
    }
  }
}
