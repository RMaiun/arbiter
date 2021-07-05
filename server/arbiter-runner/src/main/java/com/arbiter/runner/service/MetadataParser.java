package com.arbiter.runner.service;

import com.arbiter.flows.dto.BotInputMessage;
import com.arbiter.runner.exception.MetadataParserException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.stereotype.Service;

@Service
public class MetadataParser {

  private final ObjectMapper mapper;

  public MetadataParser(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  public BotInputMessage parseCommand(byte[] body) {
    try {
      return mapper.readValue(body, BotInputMessage.class);
    } catch (IOException e) {
      throw new MetadataParserException(e);
    }
  }
}
