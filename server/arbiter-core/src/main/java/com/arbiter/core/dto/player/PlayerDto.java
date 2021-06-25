package com.arbiter.core.dto.player;

import com.arbiter.core.domain.Achievement;
import com.arbiter.core.domain.Player;
import java.util.List;

public record PlayerDto(String id,
                        String surname,
                        String tid,
                        boolean admin,
                        boolean notificationsEnabled,
                        boolean active,
                        List<Achievement> achievements) {

  public static PlayerDto fromPlayer(Player p) {
    return new PlayerDto(p.getId(), p.getSurname(), p.getTid(), p.isAdmin(),
        p.isNotificationsEnabled(), p.isActive(), p.getAchievements());
  }
}
