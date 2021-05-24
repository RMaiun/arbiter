package com.arbiter.core.config;

import com.arbiter.core.dto.AlgorithmType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppProperties {

  @Value("${app.topPlayersLimit}")
  public int topPlayersLimit;

  @Value("${app.winPoints}")
  public int winPoints;

  @Value("${app.winShutoutPoints}")
  public int winShutoutPoints;

  @Value("${app.losePoints}")
  public int losePoints;

  @Value("${app.loseShutoutPoints}")
  public int loseShutoutPoints;

  @Value("${app.algorithm}")
  public AlgorithmType algorithm;

  @Value("${app.archiveReceiver}")
  public String archiveReceiver;

  @Value("${app.notificationsEnabled}")
  public boolean notificationsEnabled;

  @Value("${app.expectedGames}")
  public int expectedGames;

  @Value("${app.reportTimezone}")
  public String reportTimezone;

  @Value("${app.privileged}")
  public String privileged;

  @Value("${mongo.url}")
  public String mongoUrl;

  @Value("${mongo.db}")
  public String mongoDb;
}
