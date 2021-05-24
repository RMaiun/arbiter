package com.arbiter.http.controller;


import com.arbiter.core.dto.stats.SeasonShortStats;
import com.arbiter.core.dto.stats.SeasonStatsRows;
import com.arbiter.core.service.StatisticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("stats")
public class StatsController {

  private final StatisticsService statisticsService;

  public StatsController(StatisticsService statisticsService) {
    this.statisticsService = statisticsService;
  }

  @GetMapping("/table/{season}")
  public SeasonStatsRows seasonRowsStatistic(@PathVariable String season) {
    return statisticsService.seasonStatisticsRows(season);
  }

  @GetMapping("/short/{season}")
  public SeasonShortStats generalSeasonStatistics(@PathVariable String season) {
    return statisticsService.seasonShortInfoStatistics(season);
  }
}
