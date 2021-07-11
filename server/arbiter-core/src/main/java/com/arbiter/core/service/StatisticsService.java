package com.arbiter.core.service;

import com.arbiter.core.config.AppProperties;
import com.arbiter.core.dto.stats.SeasonExtendedStats;
import com.arbiter.core.dto.stats.SeasonShortStats;
import com.arbiter.core.dto.stats.SeasonStatsForPlayerDtoOut;
import com.arbiter.core.dto.stats.SeasonStatsRows;
import com.arbiter.core.validation.ValidationTypes;
import com.arbiter.core.validation.Validator;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService {

  public static final String N_A = "n/a";
  private final RoundsService roundsService;
  private final StatsCalculator statsCalculator;
  private final AppProperties appProperties;
  private final SeasonService seasonService;

  public StatisticsService(RoundsService roundsService, StatsCalculator statsCalculator, AppProperties appProperties, SeasonService seasonService) {
    this.roundsService = roundsService;
    this.statsCalculator = statsCalculator;
    this.appProperties = appProperties;
    this.seasonService = seasonService;
  }

  public SeasonStatsRows seasonStatisticsRows(String seasonName) {
    Validator.validate(seasonName, ValidationTypes.seasonValidationType);
    var rounds = roundsService.findAllRounds(seasonName);
    return statsCalculator.prepareSeasonStatsTable(rounds);
  }

  public SeasonShortStats seasonShortInfoStatistics(String seasonName) {
    Validator.validate(seasonName, ValidationTypes.seasonValidationType);
    var allRounds = roundsService.findAllRounds(seasonName);
    return statsCalculator.prepareSeasonShortStats(seasonName, allRounds, appProperties.algorithm);
  }

  public SeasonStatsForPlayerDtoOut allSeasonsStatsForPlayer(String player) {
    var allSeasons = seasonService.findAllSeasons();
    var allSeasonStats = allSeasons.stream().map(s -> {
      var stats = seasonShortInfoStatistics(s.getName());
      var winner = CollectionUtils.isEmpty(stats.playersRating()) ? N_A : stats.playersRating().get(0).surname();
      var bs = stats.bestStreak();
      var ws = stats.worstStreak();
      return new SeasonExtendedStats(s.getName(), winner, bs, ws);
    }).collect(Collectors.toList());
    return new SeasonStatsForPlayerDtoOut(player, allSeasonStats);
  }
}
