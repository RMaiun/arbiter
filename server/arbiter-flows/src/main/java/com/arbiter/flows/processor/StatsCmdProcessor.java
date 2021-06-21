package com.arbiter.flows.processor;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.capitalize;

import com.arbiter.core.config.AppProperties;
import com.arbiter.core.dto.season.SeasonDto;
import com.arbiter.core.dto.stats.SeasonShortStats;
import com.arbiter.core.service.StatisticsService;
import com.arbiter.flows.dto.BotInputMessage;
import com.arbiter.flows.dto.OutputMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class StatsCmdProcessor implements CommandProcessor {

  private final ObjectMapper mapper;
  private final StatisticsService statisticsService;
  private final AppProperties appProps;

  public StatsCmdProcessor(ObjectMapper mapper, StatisticsService statisticsService, AppProperties appProps) {
    this.mapper = mapper;
    this.statisticsService = statisticsService;
    this.appProps = appProps;
  }

  @Override
  public OutputMessage process(BotInputMessage input, int msgId) {
    var data = mapper.convertValue(input.data(), SeasonDto.class);
    var dto = statisticsService.seasonShortInfoStatistics(data.season());
    var str = format(dto);
    return OutputMessage.ok(input.chatId(), msgId, str);
  }

  @Override
  public List<String> commands() {
    return List.of(SHORT_STATS_CMD);
  }

  private String format(SeasonShortStats data) {
    if (data.gamesPlayed() == 0) {
      return String.format("%sНе знайдено ігор в сезоні %s%s", PREFIX, data.season(), SUFFIX);
    }

    String ratings = data.playersRating().isEmpty()
        ? String.format("Ніхто не зіграв більше %d ігор", appProps.expectedGames)
        : IntStream.range(0, data.playersRating().size())
            .mapToObj(i -> String.format("%d. %s %s", i + 1,
                capitalize(data.playersRating().get(i).surname()),
                ratingToPercents(data.playersRating().get(i).score())))
            .collect(Collectors.joining(LINE_SEPARATOR));

    String bestStreak = String.format("%s: %d ігор підряд", capitalize(data.bestStreak().player()), data.bestStreak().games());
    String worstStreak = String.format("%s: %d ігор підряд", capitalize(data.worstStreak().player()), data.worstStreak().games());
    String separator = StringUtils.repeat("-", 30);
    return PREFIX
        + "Сезон: " + data.season() + LINE_SEPARATOR
        + "Ігор зіграно: " + data.gamesPlayed() + LINE_SEPARATOR
        + "Залишилось днів: " + data.daysToSeasonEnd() + LINE_SEPARATOR
        + separator + LINE_SEPARATOR
        + "Рейтинг:" + LINE_SEPARATOR
        + ratings + LINE_SEPARATOR
        + formatUnrankedPlayers(data, separator)
        + separator + LINE_SEPARATOR
        + "Найкраща серія:" + LINE_SEPARATOR
        + bestStreak + LINE_SEPARATOR
        + separator + LINE_SEPARATOR
        + "Найгірша серія:" + LINE_SEPARATOR
        + worstStreak + LINE_SEPARATOR
        + SUFFIX;
  }

  private String formatUnrankedPlayers(SeasonShortStats data, String separator) {
    if (data.unrankedStats().isEmpty()) {
      return EMPTY;
    }
    String unrankedData = data.unrankedStats().stream()
        .map(us -> String.format("- %s +%d", capitalize(us.player()), us.gamesToPlay()))
        .collect(Collectors.joining(LINE_SEPARATOR));
    return separator + LINE_SEPARATOR
        + "Чекаємо у рейтингу:" + LINE_SEPARATOR
        + unrankedData + LINE_SEPARATOR;
  }

  private String ratingToPercents(String rating) {
    return rating + PERCENT;
  }
}
