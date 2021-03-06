package com.arbiter.flows.processor;


import com.arbiter.core.domain.Player;
import com.arbiter.core.dto.player.FoundPlayers;
import com.arbiter.core.dto.player.PlayerDto;
import com.arbiter.core.service.PlayerService;
import com.arbiter.rabbit.dto.BotInputMessage;
import com.arbiter.rabbit.dto.OutputMessage;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class ListPlayersProcessor implements CommandProcessor {

  private final PlayerService playerService;

  public ListPlayersProcessor(PlayerService playerService) {
    this.playerService = playerService;
  }

  @Override
  public OutputMessage process(BotInputMessage input, int msgId) {
    var moderator = playerService.findPlayerByTid(input.tid());
    var data = playerService.findAllPlayers(!moderator.isAdmin());
    var str = format(data, moderator);
    return OutputMessage.ok(input.chatId(), msgId, str);
  }

  @Override
  public List<String> commands() {
    return List.of(LIST_PLAYERS_CMD);
  }

  private String format(FoundPlayers data, Player moderator) {
    if (data.players().isEmpty()) {
      return String.format("%sНе знайдено активних гравців%s", PREFIX, SUFFIX);
    }
    String players = IntStream.range(0, data.players().size())
        .mapToObj(i -> String.format("%d|%s", i + 1, formatPlayerLine(data.players().get(i), moderator)))
        .collect(Collectors.joining("\n"));
    return String.format("%s%s%s", PREFIX, players, SUFFIX);
  }

  private String formatPlayerLine(PlayerDto playerDto, Player moderator) {
    if (moderator.isActive() && moderator.isAdmin()) {
      var admin = playerDto.admin() ? 1 : 0;
      var notifications = playerDto.notificationsEnabled() ? 1 : 0;
      var active = playerDto.active() ? 1 : 0;
      var player = StringUtils.capitalize(playerDto.surname());
      return String.format("%s [%d%d%d]", player, admin, notifications, active);
    } else {
      return StringUtils.capitalize(playerDto.surname());
    }
  }
}
