package com.arbiter.flows.postprocessor;

import com.arbiter.core.dto.subscription.LinkTidDto;
import com.arbiter.rabbit.dto.BotInputMessage;
import com.arbiter.rabbit.dto.BotOutputMessage;
import com.arbiter.rabbit.dto.OutputMessage;
import com.arbiter.rabbit.service.RabbitSender;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionPostProcessor implements PostProcessor {

  private final ObjectMapper mapper;
  private final RabbitSender rabbitSender;

  public SubscriptionPostProcessor(ObjectMapper mapper, RabbitSender rabbitSender) {
    this.mapper = mapper;
    this.rabbitSender = rabbitSender;
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
    rabbitSender.send(OutputMessage.ok(dto));
  }
}
