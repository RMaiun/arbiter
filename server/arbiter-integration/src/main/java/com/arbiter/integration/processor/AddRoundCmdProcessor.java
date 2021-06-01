package com.arbiter.integration.processor;

import com.arbiter.core.dto.IdDto;
import com.arbiter.core.dto.round.AddRoundDto;
import com.arbiter.core.service.RoundsService;
import com.arbiter.integration.dto.BotInputMessage;
import com.arbiter.integration.dto.OutputMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AddRoundCmdProcessor implements CommandProcessor {

  private final ObjectMapper mapper;
  private final RoundsService roundsService;

  public AddRoundCmdProcessor(ObjectMapper mapper, RoundsService roundsService) {
    this.mapper = mapper;
    this.roundsService = roundsService;
  }

  @Override
  public OutputMessage process(BotInputMessage input, int msgId) {
    var data = mapper.convertValue(input.data(), AddRoundDto.class);
    var idDto = roundsService.saveRound(data);
    var str = format(idDto);
    return OutputMessage.ok(input.chatId(), msgId, str);
  }

  @Override
  public List<String> commands() {
    return List.of(ADD_ROUND_CMD);
  }

  private String format(IdDto data) {
    var id = data.id().substring(data.id().length() - 4);
    return String.format("%s New round was stored with id %s %s",
        PREFIX, id, SUFFIX);
  }
}
