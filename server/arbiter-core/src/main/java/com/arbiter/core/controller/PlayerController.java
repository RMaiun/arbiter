package com.arbiter.core.controller;

import com.arbiter.core.dto.IdDto;
import com.arbiter.core.dto.player.AddAchievementDto;
import com.arbiter.core.dto.player.AddPlayerDto;
import com.arbiter.core.dto.player.FoundPlayers;
import com.arbiter.core.service.PlayerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("players")
public class PlayerController {

  private final PlayerService playerService;

  public PlayerController(PlayerService playerService) {
    this.playerService = playerService;
  }

  @GetMapping("/all")
  public FoundPlayers findAllPlayers() {
    return playerService.findAllPlayers(false);
  }

  @PostMapping("/achievement/add")
  public void addAchievement(AddAchievementDto dto) {
    playerService.addAchievement(dto);
  }

  @PostMapping("/add")
  public IdDto addPlayer(@RequestBody AddPlayerDto addPlayerDto) {
    return playerService.addPlayer(addPlayerDto);
  }

  @PostMapping("/achievement/add")
  public void addPlayer(@RequestBody AddAchievementDto dto) {
    playerService.addAchievement(dto);
  }
}
