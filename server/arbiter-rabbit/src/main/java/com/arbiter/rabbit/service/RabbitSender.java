package com.arbiter.rabbit.service;

import com.arbiter.rabbit.config.RabbitProperties;
import com.arbiter.rabbit.dto.AchievementEvent;
import com.arbiter.rabbit.dto.OutputMessage;
import com.arbiter.rabbit.exception.RabbitSenderException;
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
        String data = objectMapper.writeValueAsString(msg.data());
        rabbitTemplate.send("", rabbitProps.getOutputQueue(), new Message(data.getBytes()));
      }
    } catch (JsonProcessingException e) {
      throw new RabbitSenderException(e);
    }
  }

  public void fireEvent(AchievementEvent event) {
    try {
      String data = objectMapper.writeValueAsString(event);
      rabbitTemplate.send("", rabbitProps.getEventQueue(), new Message(data.getBytes()));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }
}
