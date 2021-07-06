package com.arbiter.flows.postprocessor;

import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.capitalize;

import com.arbiter.core.dto.round.AddRoundDto;
import com.arbiter.core.service.PlayerService;
import com.arbiter.rabbit.dto.BotInputMessage;
import com.arbiter.rabbit.dto.BotOutputMessage;
import com.arbiter.rabbit.dto.OutputMessage;
import com.arbiter.rabbit.service.RabbitSender;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Service;

@Service
public class AddRoundPostProcessor implements PostProcessor {

  private final ObjectMapper mapper;
  private final PlayerService playerService;
  private final RabbitSender rabbitSender;

  public AddRoundPostProcessor(ObjectMapper mapper, PlayerService playerService, RabbitSender rabbitSender) {
    this.mapper = mapper;
    this.playerService = playerService;
    this.rabbitSender = rabbitSender;
  }

  @Override
  public List<String> commands() {
    return Collections.singletonList(ADD_ROUND_CMD);
  }

  @Override
  public void postProcess(BotInputMessage input, int msgId) {
    var dto = parse(input.data());
    var dataList = List.of(
        Triple.of(dto.w1(), format("%s/%s", capitalize(dto.l1()), capitalize(dto.l2())), true),
        Triple.of(dto.w2(), format("%s/%s", capitalize(dto.l1()), capitalize(dto.l2())), true),
        Triple.of(dto.l1(), format("%s/%s", capitalize(dto.w1()), capitalize(dto.w2())), false),
        Triple.of(dto.l2(), format("%s/%s", capitalize(dto.w1()), capitalize(dto.w2())), false)
    );
    dataList.forEach(el -> sendNotificationToUser(msgId, el.getLeft(), el.getRight(), el.getMiddle()));

  }

  private AddRoundDto parse(Map<String, Object> data) {
    return mapper.convertValue(data, AddRoundDto.class);
  }

  private void sendNotificationToUser(int msgId, String player, boolean winner, String opponents) {
    var p = playerService.findPlayerByName(player);
    if (p.isNotificationsEnabled() && nonNull(p.getTid())) {
      var dto = new BotOutputMessage(p.getTid(), msgId, formatNotification(opponents, winner));
      rabbitSender.send(OutputMessage.ok(dto));
    }
  }

  private String formatNotification(String opponents, boolean winner) {
    String action = winner ? "Виграш" : "Програш";
    return format("%s%s проти %s було збережено%s",
        PREFIX, action, capitalize(opponents), SUFFIX);
  }
}
