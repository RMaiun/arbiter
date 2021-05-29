package com.arbiter.integration.service;

import com.arbiter.integration.dto.BotInputMessage;
import com.arbiter.integration.exception.MetadataParserException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.stereotype.Service;

@Service
public class MetadataParser {

  private final ObjectMapper mapper;

  public MetadataParser(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  BotInputMessage parseCommand(byte[] body) {
    try {
      return mapper.readValue(body, BotInputMessage.class);
    } catch (IOException e) {
      throw new MetadataParserException(e);
    }
  }
}
