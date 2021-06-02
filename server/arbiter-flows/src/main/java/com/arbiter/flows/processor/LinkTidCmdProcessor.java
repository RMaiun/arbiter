package com.arbiter.flows.processor;

import com.arbiter.core.dto.subscription.LinkTidDto;
import com.arbiter.core.dto.subscription.SubscriptionResultDto;
import com.arbiter.core.service.SubscriptionService;
import com.arbiter.flows.dto.BotInputMessage;
import com.arbiter.flows.dto.OutputMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class LinkTidCmdProcessor implements CommandProcessor {

  private final ObjectMapper mapper;
  private final SubscriptionService subscriptionService;

  public LinkTidCmdProcessor(ObjectMapper mapper, SubscriptionService subscriptionService) {
    this.mapper = mapper;
    this.subscriptionService = subscriptionService;
  }

  @Override
  public List<String> commands() {
    return List.of(LINK_TID_CMD);
  }

  @Override
  public OutputMessage process(BotInputMessage input, int msgId) {
    var data = mapper.convertValue(input.data(), LinkTidDto.class);
    var dto = subscriptionService.linkTidForPlayer(data);
    var str = format(dto);
    return OutputMessage.ok(input.chatId(), msgId, str);
  }

  private String format(SubscriptionResultDto data) {
    return String.format("%s Notifications were linked for %s%s", PREFIX, data.subscribedSurname(), SUFFIX);
  }

}
