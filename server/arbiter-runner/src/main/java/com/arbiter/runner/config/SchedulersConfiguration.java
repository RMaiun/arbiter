package com.arbiter.runner.config;


import com.arbiter.runner.service.HealthCheckService;
import com.arbiter.runner.service.SeasonInitializer;
import com.arbiter.runner.service.SeasonStatsSender;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
public class SchedulersConfiguration {

  private final SeasonStatsSender seasonStatsSender;
  private final SeasonInitializer seasonInitializer;
  private final HealthCheckService healthCheckService;

  public SchedulersConfiguration(SeasonStatsSender seasonStatsSender, SeasonInitializer seasonInitializer, HealthCheckService healthCheckService) {
    this.seasonStatsSender = seasonStatsSender;
    this.seasonInitializer = seasonInitializer;
    this.healthCheckService = healthCheckService;
  }

  @Scheduled(cron = "0 0 * * * ?")
  public void finalSeasonReportNotifications() {
    seasonStatsSender.sendFinalSeasonStats();
  }

  @Scheduled(cron = "0 0 * 1 * ?")
  public void initSeason() {
    seasonInitializer.initSeason();
  }

  @Scheduled(cron = "0 0 */4 * * ?")
  public void adminHealthCheck() {
    healthCheckService.healthCheck();
  }
}
