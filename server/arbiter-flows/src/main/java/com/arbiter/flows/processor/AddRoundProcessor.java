package com.arbiter.flows.processor;

import com.arbiter.core.dto.IdDto;
import com.arbiter.core.dto.round.AddRoundDto;
import com.arbiter.core.service.RoundsService;
import com.arbiter.rabbit.dto.BotInputMessage;
import com.arbiter.rabbit.dto.OutputMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AddRoundProcessor implements CommandProcessor {

  private final ObjectMapper mapper;
  private final RoundsService roundsService;

  public AddRoundProcessor(ObjectMapper mapper, RoundsService roundsService) {
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
    return String.format("%s Додано нову гру з id %s %s",
        PREFIX, id, SUFFIX);
  }
}
