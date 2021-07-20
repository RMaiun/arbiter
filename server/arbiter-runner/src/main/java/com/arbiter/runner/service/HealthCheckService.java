package com.arbiter.runner.service;

import com.arbiter.core.config.AppProperties;
import com.arbiter.core.service.PlayerService;
import com.arbiter.flows.utils.IdGenerator;
import com.arbiter.rabbit.dto.BotOutputMessage;
import com.arbiter.rabbit.dto.OutputMessage;
import com.arbiter.rabbit.service.RabbitSender;
import org.springframework.stereotype.Service;

@Service
public class HealthCheckService {

  public static final String PING_MSG = "``` I'm alive```";
  private final RabbitSender rabbitSender;
  private final AppProperties appProperties;
  private final PlayerService playerService;

  public HealthCheckService(RabbitSender rabbitSender, AppProperties appProperties, PlayerService playerService) {
    this.rabbitSender = rabbitSender;
    this.appProperties = appProperties;
    this.playerService = playerService;
  }

  public void healthCheck() {
    var player = playerService.findPlayerByName(appProperties.archiveReceiver);
    var data = new BotOutputMessage(player.getTid(), IdGenerator.msgId(), PING_MSG);
    rabbitSender.send(OutputMessage.ok(data));
  }
}
