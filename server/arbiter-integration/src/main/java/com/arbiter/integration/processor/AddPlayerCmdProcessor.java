package com.arbiter.integration.processor;

import com.arbiter.core.dto.IdDto;
import com.arbiter.core.dto.player.AddPlayerDto;
import com.arbiter.core.service.PlayerService;
import com.arbiter.integration.dto.BotInputMessage;
import com.arbiter.integration.dto.OutputMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AddPlayerCmdProcessor implements CommandProcessor {

  private final PlayerService playerService;
  private final ObjectMapper mapper;

  public AddPlayerCmdProcessor(PlayerService playerService, ObjectMapper mapper) {
    this.playerService = playerService;
    this.mapper = mapper;
  }

  @Override
  public OutputMessage process(BotInputMessage input, int msgId) {
    var data = mapper.convertValue(input.data(), AddPlayerDto.class);
    var idDto = playerService.addPlayer(data);
    var str = format(idDto);
    return OutputMessage.ok(input.chatId(), msgId, str);
  }

  @Override
  public List<String> commands() {
    return Collections.singletonList(ADD_PLAYER_CMD);
  }

  private String format(IdDto data) {
    return String.format("%s New player was stored with id %s %s",
        PREFIX, data.id(), SUFFIX);
  }
}
