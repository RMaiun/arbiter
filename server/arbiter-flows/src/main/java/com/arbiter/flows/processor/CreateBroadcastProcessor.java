package com.arbiter.flows.processor;

import com.arbiter.core.config.AppProperties;
import com.arbiter.core.dto.broadcast.StoreBroadcastDto;
import com.arbiter.core.dto.player.FoundPlayers;
import com.arbiter.core.dto.player.PlayerDto;
import com.arbiter.core.service.PlayerService;
import com.arbiter.flows.service.BroadcastService;
import com.arbiter.flows.utils.IdGenerator;
import com.arbiter.rabbit.dto.BotInputMessage;
import com.arbiter.rabbit.dto.OutputMessage;
import com.arbiter.rabbit.service.RabbitSender;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CreateBroadcastProcessor implements CommandProcessor {

  private final ObjectMapper mapper;
  private final BroadcastService broadcastService;
  private final PlayerService playerService;
  private final RabbitSender rabbitSender;
  private final AppProperties appProperties;

  public CreateBroadcastProcessor(ObjectMapper mapper, BroadcastService broadcastService, PlayerService playerService, RabbitSender rabbitSender,
      AppProperties appProperties) {
    this.mapper = mapper;
    this.broadcastService = broadcastService;
    this.playerService = playerService;
    this.rabbitSender = rabbitSender;
    this.appProperties = appProperties;
  }

  @Override
  public OutputMessage process(BotInputMessage input, int msgId) {
    var data = mapper.convertValue(input.data(), StoreBroadcastDto.class);
    var broadcastData = broadcastService.storeBroadcast(data);
    var messageToSend = String.format("%s %s %s", PREFIX, broadcastData.message(), SUFFIX);
    FoundPlayers allPlayers = playerService.findAllPlayers(true);
    if (appProperties.notificationsEnabled) {
      allPlayers.players()
          .stream()
          .filter(PlayerDto::notificationsEnabled)
          .forEach(p -> rabbitSender.send(OutputMessage.ok(p.tid(), IdGenerator.msgId(), messageToSend)));
    }
    return OutputMessage.ok(input.chatId(), IdGenerator.msgId(), DEFAULT_RESULT);
  }

  @Override
  public List<String> commands() {
    return List.of(BROADCAST_MSG_CMD);
  }
}
