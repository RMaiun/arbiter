package com.arbiter.core.service;


import com.arbiter.core.config.AppProperties;
import com.arbiter.core.domain.Player;
import com.arbiter.core.exception.AuthorizationRuntimeException;
import com.arbiter.core.exception.InvalidUserRightsException;
import com.arbiter.core.repository.PlayerRepository;
import java.util.List;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

@Service
public class UserRightsService {

  private final PlayerRepository playerRepository;
  private final AppProperties appProperties;

  public UserRightsService(PlayerRepository playerRepository, AppProperties appProperties) {
    this.playerRepository = playerRepository;
    this.appProperties = appProperties;
  }

  public void checkUserIsAdmin(String tid) {
    if (!tid.equals(appProperties.privileged)) {
      var players = playerRepository.listAll();
      checkAdminPermissions(players, tid);
    }
  }

  public void checkUserIsRegistered(String tid) {
    var player = playerRepository.getPlayerByCriteria(Criteria.where("tid").is(tid));
    if (player.isEmpty()) {
      throw new AuthorizationRuntimeException();
    }
  }

  private void checkAdminPermissions(List<Player> players, String tid) {
    var foundPlayer = players.stream()
        .filter(p -> tid.equals(p.getTid()) && p.isAdmin())
        .findAny();
    if (foundPlayer.isEmpty()) {
      throw new InvalidUserRightsException();
    }
  }
}
