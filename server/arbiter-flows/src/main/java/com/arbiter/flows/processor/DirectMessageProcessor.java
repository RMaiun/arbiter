package com.arbiter.flows.processor;

import com.arbiter.core.service.PlayerService;
import com.arbiter.flows.dto.DirectMessageDto;
import com.arbiter.flows.utils.Constants;
import com.arbiter.flows.utils.IdGenerator;
import com.arbiter.rabbit.dto.BotInputMessage;
import com.arbiter.rabbit.dto.OutputMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class DirectMessageProcessor implements CommandProcessor {

  public static final Logger log = LogManager.getLogger(DirectMessageProcessor.class);

  private final ObjectMapper mapper;
  private final PlayerService playerService;

  public DirectMessageProcessor(ObjectMapper mapper, PlayerService playerService) {
    this.mapper = mapper;
    this.playerService = playerService;
  }

  @Override
  public OutputMessage process(BotInputMessage input, int msgId) {
    var data = mapper.convertValue(input.data(), DirectMessageDto.class);
    var sender = playerService.findPlayerByName(data.sender());
    var receiver = playerService.findPlayerByName(data.receiver());
    receiver.setActive(false);
    if (sender.isAdmin() && receiver.isActive() && receiver.isNotificationsEnabled() && receiver.getTid() != null) {
      return OutputMessage.ok(receiver.getTid(), IdGenerator.msgId(), data.text());
    } else if (sender.isAdmin()) {
      var msg = String.format("Cant send direct message to %s: player.isAdmin=%s, receiver.isActive=%s, receiver.isNotificationsEnabled=%s",
          receiver.getSurname(), sender.isAdmin(), receiver.isActive(), receiver.isNotificationsEnabled());
      log.warn(msg);
      return OutputMessage.error(sender.getTid(), IdGenerator.msgId(), data.text());
    } else {
      return OutputMessage.error(input.chatId(), IdGenerator.msgId(), Constants.DEFAULT_RESULT);
    }
  }

  @Override
  public List<String> commands() {
    return List.of(DIRECT_MSG_CMD);
  }
}
