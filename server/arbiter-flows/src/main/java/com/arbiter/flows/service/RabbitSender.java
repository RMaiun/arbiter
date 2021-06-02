package com.arbiter.flows.service;

import com.arbiter.flows.config.RabbitProperties;
import com.arbiter.flows.dto.OutputMessage;
import com.arbiter.flows.exception.RabbitSenderException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class RabbitSender {

  private final RabbitTemplate rabbitTemplate;
  private final ObjectMapper objectMapper;
  private final RabbitProperties rabbitProps;

  public RabbitSender(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper, RabbitProperties rabbitProps) {
    this.rabbitTemplate = rabbitTemplate;
    this.objectMapper = objectMapper;
    this.rabbitProps = rabbitProps;
  }

  public void send(OutputMessage msg) {
    try {
      if (!msg.data().result().isEmpty()) {
        String data =  objectMapper.writeValueAsString(msg.data());
        rabbitTemplate.send("", rabbitProps.getOutputQueue(), new Message(data.getBytes()));
      }
    } catch (JsonProcessingException e) {
      throw new RabbitSenderException(e);
    }

  }
}
