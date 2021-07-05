package com.arbiter.flows.service;

import com.arbiter.flows.dto.BotOutputMessage;
import com.arbiter.flows.exception.JsonSerializationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class SafeJsonMapper {

  private final ObjectMapper mapper;

  public SafeJsonMapper(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  public String outputMsgtoJson(BotOutputMessage msg) {
    try {
      return mapper.writeValueAsString(msg);
    } catch (JsonProcessingException e) {
      throw new JsonSerializationException(e);
    }
  }

}
