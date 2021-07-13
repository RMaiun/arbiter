package com.arbiter.flows.processor;

import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.springframework.util.CollectionUtils.isEmpty;

import com.arbiter.core.dto.round.FindLastRoundsDto;
import com.arbiter.core.dto.round.FoundLastRounds;
import com.arbiter.core.dto.round.FullRound;
import com.arbiter.core.service.RoundsService;
import com.arbiter.core.utils.DateUtils;
import com.arbiter.rabbit.dto.BotInputMessage;
import com.arbiter.rabbit.dto.OutputMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class LastRoundsProcessor implements CommandProcessor {

  private final ObjectMapper mapper;
  private final RoundsService roundsService;

  public LastRoundsProcessor(ObjectMapper mapper, RoundsService roundsService) {
    this.mapper = mapper;
    this.roundsService = roundsService;
  }

  @Override
  public OutputMessage process(BotInputMessage input, int msgId) {
    var data = mapper.convertValue(input.data(), FindLastRoundsDto.class);
    var rounds = roundsService.findLastRoundsInSeason(data);
    var str = format(rounds);
    return OutputMessage.ok(input.chatId(), msgId, str);
  }

  @Override
  public List<String> commands() {
    return List.of(FIND_LAST_ROUNDS_CMD);
  }

  private String format(FoundLastRounds data) {
    if (isEmpty(data.rounds())) {
      return String.format("%s Не знайдено ігор в сезоні %s%s", PREFIX, data.season(), SUFFIX);
    } else {
      return data.rounds().stream()
          .map(this::formatRound)
          .collect(Collectors.joining(DELIMITER, PREFIX, SUFFIX));
    }
  }

  private String formatRound(FullRound round) {
    String date = DateUtils.formatDateWithHour(round.created());
    String winners = String.format("%s/%s", capitalize(round.winner1()), capitalize(round.winner2()));
    String losers = String.format("%s/%s", capitalize(round.loser1()), capitalize(round.loser2()));
    StringBuilder sb = new StringBuilder();
    sb.append("дата: ").append(date).append(LINE_SEPARATOR);
    sb.append("красави: ").append(winners).append(LINE_SEPARATOR);
    sb.append("лузери: ").append(losers).append(LINE_SEPARATOR);
    if (round.shutout()) {
      sb.append("суха: ✓").append(LINE_SEPARATOR);
    }
    return sb.toString();
  }
}
