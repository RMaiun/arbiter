package com.arbiter.rabbit.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.AlwaysRetryPolicy;
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
    retryTemplate.setRetryPolicy(new AlwaysRetryPolicy());
    ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
    backOffPolicy.setInitialInterval(500);
    backOffPolicy.setMultiplier(10.0);
    backOffPolicy.setMaxInterval(10000);
    retryTemplate.setBackOffPolicy(backOffPolicy);
    rabbitTemplate.setRetryTemplate(retryTemplate);
    return rabbitTemplate;
  }

  @Bean
  public Queue inputQueue() {
    return new Queue(rabbitProperties.getInputQueue(), false, false, false);
  }

  @Bean
  public Queue outputQueue() {
    return new Queue(rabbitProperties.getOutputQueue(), false, false, false);
  }

  @Bean
  public Queue eventQueue() {
    return new Queue(rabbitProperties.getEventQueue(), false, false, false);
  }

}
