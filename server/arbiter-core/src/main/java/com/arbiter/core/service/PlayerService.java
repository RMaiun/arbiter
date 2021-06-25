package com.arbiter.core.service;

import static com.arbiter.core.validation.Validator.validate;

import com.arbiter.core.domain.Achievement;
import com.arbiter.core.domain.Player;
import com.arbiter.core.dto.IdDto;
import com.arbiter.core.dto.player.AbsentPlayersDto;
import com.arbiter.core.dto.player.ActionAck;
import com.arbiter.core.dto.player.ActivatePlayersDto;
import com.arbiter.core.dto.player.AddAchievementDtoIn;
import com.arbiter.core.dto.player.AddAchievementDtoOut;
import com.arbiter.core.dto.player.AddPlayerDto;
import com.arbiter.core.dto.player.FoundPlayers;
import com.arbiter.core.dto.player.PlayerDto;
import com.arbiter.core.exception.PlayerAlreadyExistsException;
import com.arbiter.core.exception.PlayerNotFoundException;
import com.arbiter.core.exception.PlayersNotFoundException;
import com.arbiter.core.repository.PlayerRepository;
import com.arbiter.core.utils.DateUtils;
import com.arbiter.core.validation.ValidationTypes;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {

  public static final Logger log = LogManager.getLogger(PlayerService.class);

  private final PlayerRepository playerRepository;
  private final UserRightsService userRightsService;

  public PlayerService(PlayerRepository playerRepository, UserRightsService userRightsService) {
    this.playerRepository = playerRepository;
    this.userRightsService = userRightsService;
  }

  public FoundPlayers findAllPlayers(boolean onlyActive) {
    var players = playerRepository.listAll(onlyActive)
        .stream()
        .map(PlayerDto::fromPlayer)
        .collect(Collectors.toList());
    log.info("Found {} players", players.size());
    return new FoundPlayers(players);
  }

  public AbsentPlayersDto updateActiveStatusPlayers(ActivatePlayersDto dto, boolean activate) {
    List<String> absentPlayers = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(dto.players())) {
      dto.players().forEach(surname -> {
        var player = playerRepository.getPlayer(surname.toLowerCase());
        if (player.isPresent()) {
          var p = player.get();
          p.setActive(activate);
          playerRepository.updatePlayer(p);
        } else {
          absentPlayers.add(surname);
        }
      });
    }
    return new AbsentPlayersDto(absentPlayers);
  }

  public List<Player> checkPlayersExist(List<String> surnameList) {
    var lowerCasedSurnames = lowercaseSurnames(surnameList);
    return findAndCheckPlayers(lowerCasedSurnames);
  }

  public IdDto addPlayer(AddPlayerDto dto) {
    validate(dto, ValidationTypes.addPlayerValidationType);
    checkUserIsAdmin(dto.moderator());
    checkPlayerNotExist(dto);
    var id = savePlayer(dto);
    return new IdDto(id);
  }

  public Player findPlayerByName(String surname) {
    return playerRepository.getPlayerByCriteria(Criteria.where("surname").is(surname))
        .orElseThrow(() -> new PlayerNotFoundException(surname));
  }

  public Player findPlayerByTid(String tid) {
    return findPlayerByCriteria(Criteria.where("tid").is(tid));
  }

  public Player enableNotifications(String surname, String tid) {
    var foundPlayer = findPlayerByName(surname);
    foundPlayer.setNotificationsEnabled(true);
    foundPlayer.setTid(tid);
    return playerRepository.updatePlayer(foundPlayer);
  }

  public Player updatePlayer(Player p) {
    return playerRepository.updatePlayer(p);
  }

  public AddAchievementDtoOut addAchievement(AddAchievementDtoIn dto) {
    validate(dto, ValidationTypes.addAchievementDtoType);
    Player playerByName = findPlayerByName(dto.playerName());
    Optional<Achievement> achievement = playerByName.getAchievements().stream()
        .filter(a -> a.getCode().equals(dto.achievementCode()))
        .findAny();
    if (achievement.isEmpty()){
      playerByName.getAchievements().add(new Achievement(dto.achievementCode(), DateUtils.now()));
      updatePlayer(playerByName);
      return new AddAchievementDtoOut(ActionAck.OK);
    }else{
      return new AddAchievementDtoOut(ActionAck.NOK);
    }
  }

  private Player findPlayerByCriteria(Criteria criteria) {
    return playerRepository.getPlayerByCriteria(criteria)
        .orElseThrow(() -> new PlayerNotFoundException(criteria.toString()));
  }

  private String savePlayer(AddPlayerDto dto) {
    Player player = new Player(null, dto.surname().toLowerCase(), dto.tid(), dto.admin(), false, true);
    return playerRepository.savePlayer(player).getId();
  }

  private void checkUserIsAdmin(String moderator) {
    userRightsService.checkUserIsAdmin(moderator);
  }

  private void checkPlayerNotExist(AddPlayerDto dto) {
    var maybePlayer = playerRepository.getPlayer(dto.surname());
    if (maybePlayer.isPresent()) {
      throw new PlayerAlreadyExistsException(maybePlayer.get().getId());
    }
  }

  private List<Player> findAndCheckPlayers(List<String> surnames) {
    var players = playerRepository.findPlayers(surnames);
    return prepareCheckedPlayers(players, surnames);
  }

  private List<String> lowercaseSurnames(List<String> surnameList) {
    return surnameList.stream()
        .map(String::toLowerCase)
        .collect(Collectors.toList());
  }

  public List<Player> prepareCheckedPlayers(List<Player> players, List<String> surnames) {
    String found = players.stream()
        .map(Player::getSurname)
        .collect(Collectors.joining(","));
    String expected = String.join(",", surnames);
    log.info("Expected players: {}, found players: {}", expected, found);
    if (players.size() == surnames.size()) {
      return players;
    } else {
      List<String> foundIds = players.stream()
          .map(Player::getSurname)
          .collect(Collectors.toList());
      List<String> missedPlayers = surnames.stream()
          .filter(x -> !foundIds.contains(x))
          .collect(Collectors.toList());
      throw new PlayersNotFoundException(missedPlayers);
    }
  }
}
