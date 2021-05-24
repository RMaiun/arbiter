package com.arbiter.core.service;

import com.arbiter.core.config.AppProperties;
import com.arbiter.core.dto.stats.SeasonShortStats;
import com.arbiter.core.dto.stats.SeasonStatsRows;
import com.arbiter.core.validation.ValidationTypes;
import com.arbiter.core.validation.Validator;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService {

  private final RoundsService roundsService;
  private final StatsCalculator statsCalculator;
  private final AppProperties appProperties;

  public StatisticsService(RoundsService roundsService, StatsCalculator statsCalculator, AppProperties appProperties) {
    this.roundsService = roundsService;
    this.statsCalculator = statsCalculator;
    this.appProperties = appProperties;
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
}
