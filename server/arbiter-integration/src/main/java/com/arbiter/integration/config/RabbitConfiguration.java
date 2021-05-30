package com.arbiter.integration.config;

import com.arbiter.core.service.UserRightsService;
import com.arbiter.integration.postprocessor.PostProcessor;
import com.arbiter.integration.processor.CommandProcessor;
import com.arbiter.integration.service.CommandReceiver;
import com.arbiter.integration.service.MetadataParser;
import com.arbiter.integration.service.RabbitSender;
import java.util.List;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class RabbitConfiguration {

  private final RabbitProperties rabbitProperties;

  public RabbitConfiguration(RabbitProperties rabbitProperties) {
    this.rabbitProperties = rabbitProperties;
  }

  @Bean
  public ConnectionFactory rabbitConnectionFactory() {
    var rcf = new CachingConnectionFactory();
    rcf.setHost(rabbitProperties.getHost());
    rcf.setPort(rabbitProperties.getPort());
    rcf.setUsername(rabbitProperties.getUsername());
    rcf.setPassword(rabbitProperties.getPassword());
    rcf.setVirtualHost(rabbitProperties.getVirtualHost());
    return rcf;
  }

  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory rabbitConnectionFactory) {
    var rabbitTemplate = new RabbitTemplate(rabbitConnectionFactory);
    RetryTemplate retryTemplate = new RetryTemplate();
    ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
    backOffPolicy.setInitialInterval(500);
    backOffPolicy.setMultiplier(10.0);
    backOffPolicy.setMaxInterval(10000);
    retryTemplate.setBackOffPolicy(backOffPolicy);
    rabbitTemplate.setRetryTemplate(retryTemplate);
    return rabbitTemplate;
  }

  @Bean
  public SimpleMessageListenerContainer messageListenerContainer(MetadataParser metadataParser, RabbitSender rabbitSender,
      List<CommandProcessor> processors, List<PostProcessor> postProcessors, UserRightsService userRightsService) {
    SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
    container.setConnectionFactory(rabbitConnectionFactory());
    container.setQueueNames(rabbitProperties.getInputQueue());
    container.setMessageListener(new CommandReceiver(metadataParser, rabbitSender, processors, postProcessors, userRightsService));
    return container;
  }

  @Bean
  public Queue inputQueue() {
    return new Queue(rabbitProperties.getInputQueue(), false, false, false);
  }

  @Bean
  public Queue outputQueue() {
    return new Queue(rabbitProperties.getOutputQueue(), false, false, false);
  }
}