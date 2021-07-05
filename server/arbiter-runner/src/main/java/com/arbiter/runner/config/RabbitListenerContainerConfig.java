package com.arbiter.runner.config;

import com.arbiter.core.config.AppProperties;
import com.arbiter.core.service.UserRightsService;
import com.arbiter.flows.postprocessor.PostProcessor;
import com.arbiter.flows.processor.CommandProcessor;
import com.arbiter.rabbit.config.RabbitProperties;
import com.arbiter.rabbit.service.RabbitSender;
import com.arbiter.runner.service.CommandReceiver;
import com.arbiter.runner.service.MetadataParser;
import com.arbiter.flows.service.SafeJsonMapper;
import java.util.List;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitListenerContainerConfig {

  @Bean
  public SimpleMessageListenerContainer messageListenerContainer(MetadataParser metadataParser, RabbitSender rabbitSender,
      List<CommandProcessor> processors, List<PostProcessor> postProcessors, UserRightsService userRightsService, AppProperties appProperties,
      ConnectionFactory rabbitConnectionFactory, RabbitProperties rabbitProperties, SafeJsonMapper jsonMapper) {
    SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
    container.setConnectionFactory(rabbitConnectionFactory);
    container.setQueueNames(rabbitProperties.getInputQueue());
    container.setMessageListener(new CommandReceiver(
        metadataParser,
        rabbitSender,
        processors,
        postProcessors,
        userRightsService,
        appProperties,
        jsonMapper));
    container.setConcurrentConsumers(8);
    return container;
  }
}
