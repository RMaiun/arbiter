package com.arbiter.core;

import com.arbiter.core.config.AppProperties;
import com.arbiter.core.config.DbConfiguration;
import com.arbiter.core.repository.PlayerRepository;
import com.arbiter.core.service.UserRightsService;
import com.arbiter.core.service.XlsxWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

@TestConfiguration
@Import({AppProperties.class, DbConfiguration.class})
public class TestConfig {

  @Autowired
  private AppProperties appProperties;

  @Bean
  public XlsxWriter xlsxWriter() {
    return new XlsxWriter();
  }

  @Bean
  public PlayerRepository playerRepository(MongoTemplate mongoTemplate) {
    return new PlayerRepository(mongoTemplate);
  }

  @Bean
  public UserRightsService userRightsService(PlayerRepository playerRepository) {
    return new UserRightsService(playerRepository, appProperties);
  }
}
