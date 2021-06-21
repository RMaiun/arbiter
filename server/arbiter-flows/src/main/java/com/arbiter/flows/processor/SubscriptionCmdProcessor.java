package com.arbiter.flows.processor;

import com.arbiter.core.dto.subscription.SubscriptionActionDto;
import com.arbiter.core.dto.subscription.SubscriptionResultDto;
import com.arbiter.core.service.SubscriptionService;
import com.arbiter.flows.dto.BotInputMessage;
import com.arbiter.flows.dto.OutputMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionCmdProcessor implements CommandProcessor {

  private final ObjectMapper objectMapper;
  private final SubscriptionService subscriptionService;

  public SubscriptionCmdProcessor(ObjectMapper objectMapper, SubscriptionService subscriptionService) {
    this.objectMapper = objectMapper;
    this.subscriptionService = subscriptionService;
  }

  @Override
  public OutputMessage process(BotInputMessage input, int msgId) {
    var data = objectMapper.convertValue(input.data(), SubscriptionActionDto.class);
    var dto = subscriptionService.updateSubscriptionsStatus(data);
    var str = format(dto);
    return OutputMessage.ok(input.chatId(), msgId, str);
  }

  @Override
  public List<String> commands() {
    return List.of(SUBSCRIBE_CMD, UNSUBSCRIBE_CMD);
  }

  private String format(SubscriptionResultDto dto) {
    String action = dto.notificationsEnabled() ? "увімкнені" : "вимкнені";
    return String.format("%s Сповіщення були %s%s", PREFIX, action, SUFFIX);
  }
}
