package com.arbiter.core.dto.player;

import com.arbiter.core.domain.Player;

public record PlayerDto(String id, String surname, String tid, boolean admin, boolean notificationsEnabled) {

  public static PlayerDto fromPlayer(Player p) {
    return new PlayerDto(p.getId(), p.getSurname(), p.getTid(), p.isAdmin(), p.isNotificationsEnabled());
  }
}
