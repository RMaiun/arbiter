package com.arbiter.flows.processor;

import com.arbiter.core.dto.player.AbsentPlayersDto;
import com.arbiter.core.dto.player.ActivatePlayersDto;
import com.arbiter.core.service.PlayerService;
import com.arbiter.flows.dto.BotInputMessage;
import com.arbiter.flows.dto.OutputMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class UpdatePlayerActivationProcessor implements CommandProcessor {

  private final ObjectMapper objectMapper;
  private final PlayerService playerService;

  public UpdatePlayerActivationProcessor(ObjectMapper objectMapper, PlayerService playerService) {
    this.objectMapper = objectMapper;
    this.playerService = playerService;
  }

  @Override
  public OutputMessage process(BotInputMessage input, int msgId) {
    var data = objectMapper.convertValue(input.data(), ActivatePlayersDto.class);
    var activate = ACTIVATE_CMD.equals(input.cmd());
    var dtoOut = playerService.updateActiveStatusPlayers(data, activate);
    var result = format(dtoOut, activate);
    return OutputMessage.ok(input.chatId(), msgId, result);
  }

  private String format(AbsentPlayersDto absentPlayersDto, boolean activate) {
    var status = activate ? "activated" : "deactivated";
    var firstPart = String.format("All players were successfully %s", status);
    if (absentPlayersDto.players().isEmpty()) {
      return String.format("%s %s. %s", PREFIX, firstPart, SUFFIX);
    } else {
      var players = absentPlayersDto.players()
          .stream()
          .collect(Collectors.joining(",", "[", "]"));
      var secondPart = String.format("except %s", players);
      return String.format("%s %s %s. %s", PREFIX, firstPart, secondPart, SUFFIX);
    }
  }

  @Override
  public List<String> commands() {
    return List.of(ACTIVATE_CMD, DEACTIVATE_CMD);
  }
}
