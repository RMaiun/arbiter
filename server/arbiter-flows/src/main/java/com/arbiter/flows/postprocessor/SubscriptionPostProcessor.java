package com.arbiter.flows.postprocessor;

import com.arbiter.core.dto.subscription.LinkTidDto;
import com.arbiter.flows.dto.BotInputMessage;
import com.arbiter.flows.dto.BotOutputMessage;
import com.arbiter.flows.service.SafeJsonMapper;
import com.arbiter.rabbit.service.RabbitSender;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionPostProcessor implements PostProcessor {

  private final ObjectMapper mapper;
  private final RabbitSender rabbitSender;
  private final SafeJsonMapper jsonMapper;

  public SubscriptionPostProcessor(ObjectMapper mapper, RabbitSender rabbitSender, SafeJsonMapper jsonMapper) {
    this.mapper = mapper;
    this.rabbitSender = rabbitSender;
    this.jsonMapper = jsonMapper;
  }

  @Override
  public List<String> commands() {
    return Collections.singletonList(LINK_TID_CMD);
  }

  @Override
  public void postProcess(BotInputMessage input, int msgId) {
    var message = String.format("%s Реєстрація пройшла успішно%s", PREFIX, SUFFIX);
    var data = mapper.convertValue(input.data(), LinkTidDto.class);
    var dto = new BotOutputMessage(data.tid(), msgId, message);
    var json = jsonMapper.outputMsgtoJson(dto);
    rabbitSender.send(json);
  }
}
