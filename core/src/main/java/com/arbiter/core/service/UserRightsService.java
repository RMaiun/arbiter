package com.arbiter.core.service;


import com.arbiter.core.domain.Player;
import com.arbiter.core.exception.AuthorizationRuntimeException;
import com.arbiter.core.exception.InvalidUserRightsException;
import com.arbiter.core.exception.PlayerNotFoundException;
import com.arbiter.core.repository.PlayerRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

@Service
public class UserRightsService {

  private final PlayerRepository playerRepository;

  @Value("${privileged}")
  private String privileged;

  public UserRightsService(PlayerRepository playerRepository) {
    this.playerRepository = playerRepository;
  }

  public Player checkUserIsAdmin(String tid) {
    if (tid.equals(privileged)) {
      return playerRepository.getPlayer(privileged)
          .orElseThrow(() -> new PlayerNotFoundException(privileged));
    } else {
      var players = playerRepository.listAll();
      return checkAdminPermissions(players, tid);
    }
  }

  public Player checkUserIsRegistered(String tid) {
    return playerRepository.getPlayerByCriteria(Criteria.where("tid").is(tid))
        .orElseThrow(AuthorizationRuntimeException::new);
  }

  private Player checkAdminPermissions(List<Player> players, String tid) {
    return players.stream()
        .filter(p -> tid.equals(p.getTid()) && p.isAdmin())
        .findAny()
        .orElseThrow(InvalidUserRightsException::new);
  }
}
