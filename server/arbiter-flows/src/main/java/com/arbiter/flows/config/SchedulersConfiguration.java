package com.arbiter.flows.config;


import com.arbiter.core.service.SeasonInitializer;
import com.arbiter.flows.service.SeasonStatsSender;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
public class SchedulersConfiguration {

  private final SeasonStatsSender seasonStatsSender;
  private final SeasonInitializer seasonInitializer;

  public SchedulersConfiguration(SeasonStatsSender seasonStatsSender, SeasonInitializer seasonInitializer) {
    this.seasonStatsSender = seasonStatsSender;
    this.seasonInitializer = seasonInitializer;
  }

  @Scheduled(cron = "0 0 * * * ?")
  public void finalSeasonReportNotifications() {
    seasonStatsSender.sendFinalSeasonStats();
  }

  @Scheduled(cron = "0 0 * 1 * ?")
  public void initSeason() {
    seasonInitializer.initSeason();
  }
}
