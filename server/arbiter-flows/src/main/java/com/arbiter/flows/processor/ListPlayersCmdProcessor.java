package com.arbiter.flows.processor;


import com.arbiter.core.dto.player.FoundAllPlayers;
import com.arbiter.core.service.PlayerService;
import com.arbiter.flows.dto.BotInputMessage;
import com.arbiter.flows.dto.OutputMessage;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class ListPlayersCmdProcessor implements CommandProcessor {

  private final PlayerService playerService;

  public ListPlayersCmdProcessor(PlayerService playerService) {
    this.playerService = playerService;
  }

  @Override
  public OutputMessage process(BotInputMessage input, int msgId) {
    var data = playerService.findAllPlayers();
    var str = format(data);
    return OutputMessage.ok(input.chatId(), msgId, str);
  }

  @Override
  public List<String> commands() {
    return List.of(LIST_PLAYERS_CMD);
  }

  private String format(FoundAllPlayers data) {
    if (data.players().isEmpty()) {
      return String.format("%sNo active players were found%s", PREFIX, SUFFIX);
    }
    String players = IntStream.range(0, data.players().size())
        .mapToObj(i -> String.format("%d|%s", i + 1, StringUtils.capitalize(data.players().get(i).surname())))
        .collect(Collectors.joining("\n"));
    return String.format("%s%s%s", PREFIX, players, SUFFIX);
  }
}
