package com.arbiter.core.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

@Configuration
public class DbConfiguration {

  @Value("${mongo.url}")
  private String mongoUrl;

  @Value("${mongo.db}")
  private String mongoDb;


  @Bean
  public MongoClient mongoClient() {
    return MongoClients.create(mongoUrl);
  }

  @Bean
  public MongoTemplate reactiveMongoTemplate(MongoCustomConversions customConversions) {
    MongoTemplate mt = new MongoTemplate(mongoClient(), mongoDb);
    MappingMongoConverter mongoMapping = (MappingMongoConverter) mt.getConverter();
    mongoMapping.setTypeMapper(new DefaultMongoTypeMapper(null));
    mongoMapping.setCustomConversions(customConversions);
    mongoMapping.afterPropertiesSet();
    return mt;
  }

  @Bean
  public MongoCustomConversions customConversions() {
    List<Converter<?, ?>> converters = new ArrayList<>();
    converters.add(new ZonedDateTimeReadConverter());
    converters.add(new ZonedDateTimeWriteConverter());
    return new MongoCustomConversions(converters);
  }
}
