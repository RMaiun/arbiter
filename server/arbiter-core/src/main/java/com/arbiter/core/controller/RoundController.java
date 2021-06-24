package com.arbiter.core.controller;

import com.arbiter.core.dto.IdDto;
import com.arbiter.core.dto.round.AddRoundDto;
import com.arbiter.core.dto.round.FindLastRoundsDto;
import com.arbiter.core.dto.round.FoundLastRounds;
import com.arbiter.core.dto.round.FullRound;
import com.arbiter.core.dto.round.GetRoundDto;
import com.arbiter.core.dto.round.ListRoundsForPlayerDtoIn;
import com.arbiter.core.dto.round.ListRoundsForPlayerDtoOut;
import com.arbiter.core.service.RoundsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("rounds")
public class RoundController {

  private final RoundsService roundsService;

  public RoundController(RoundsService roundsService) {
    this.roundsService = roundsService;
  }

  @GetMapping("/findLast/{season}/{qty}")
  public FoundLastRounds findAllRounds(@PathVariable String season, @PathVariable int qty) {
    return roundsService.findLastRoundsInSeason(new FindLastRoundsDto(season, qty));
  }

  @PostMapping("/add")
  public IdDto addRound(@RequestBody AddRoundDto dto) {
    return roundsService.saveRound(dto);
  }

  @GetMapping("/get")
  public FullRound getRound(@RequestParam("roundId") String roundId) {
    return roundsService.getRound(new GetRoundDto(roundId));
  }

  @PostMapping("/listForPlayer")
  public ListRoundsForPlayerDtoOut listRoundsForPlayer(@RequestBody ListRoundsForPlayerDtoIn dtoIn){
    return roundsService.roundsForPlayer(dtoIn);
  }
}
