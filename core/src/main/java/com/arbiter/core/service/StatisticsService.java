package com.arbiter.core.service;

import com.arbiter.core.config.AppProperties;
import com.arbiter.core.dto.stats.SeasonShortStats;
import com.arbiter.core.dto.stats.SeasonStatsRows;
import com.arbiter.core.helper.StatsServiceHelper;
import com.arbiter.core.validation.ValidationTypes;
import com.arbiter.core.validation.Validator;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService {

  private final RoundsService roundsService;
  private final StatsServiceHelper statsServiceHelper;
  private final AppProperties appProperties;

  public StatisticsService(RoundsService roundsService, StatsServiceHelper statsServiceHelper, AppProperties appProperties) {
    this.roundsService = roundsService;
    this.statsServiceHelper = statsServiceHelper;
    this.appProperties = appProperties;
  }

  public SeasonStatsRows seasonStatisticsRows(String seasonName) {
    Validator.validate(seasonName, ValidationTypes.seasonValidationType);
    var rounds = roundsService.findAllRounds(seasonName);
    return statsServiceHelper.prepareSeasonStatsTable(rounds);
  }

  public SeasonShortStats seasonShortInfoStatistics(String seasonName) {
    Validator.validate(seasonName, ValidationTypes.seasonValidationType);
    var allRounds = roundsService.findAllRounds(seasonName);
    return statsServiceHelper.prepareSeasonShortStats(seasonName, allRounds, appProperties.algorithm);
  }
}
