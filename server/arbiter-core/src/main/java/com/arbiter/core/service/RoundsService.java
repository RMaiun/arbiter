package com.arbiter.core.service;


import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

import com.arbiter.core.domain.Round;
import com.arbiter.core.dto.IdDto;
import com.arbiter.core.dto.round.AddRoundDto;
import com.arbiter.core.dto.round.FindLastRoundsDto;
import com.arbiter.core.dto.round.FoundLastRounds;
import com.arbiter.core.dto.round.FullRound;
import com.arbiter.core.dto.round.GetRoundDto;
import com.arbiter.core.dto.round.ListRoundsForPlayerDtoIn;
import com.arbiter.core.dto.round.ListRoundsForPlayerDtoOut;
import com.arbiter.core.exception.RoundNotFoundException;
import com.arbiter.core.exception.SamePlayersInRoundException;
import com.arbiter.core.repository.RoundRepository;
import com.arbiter.core.utils.DateUtils;
import com.arbiter.core.utils.SeasonUtils;
import com.arbiter.core.validation.ValidationTypes;
import com.arbiter.core.validation.Validator;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;


@Service
public class RoundsService {

  private final PlayerService playerService;
  private final SeasonService seasonService;
  private final RoundRepository roundRepository;
  private final UserRightsService userRightsService;

  public RoundsService(PlayerService playerService, SeasonService seasonService, RoundRepository roundRepository, UserRightsService userRightsService) {
    this.playerService = playerService;
    this.seasonService = seasonService;
    this.roundRepository = roundRepository;
    this.userRightsService = userRightsService;
  }

  public FoundLastRounds findLastRoundsInSeason(FindLastRoundsDto dto) {
    Validator.validate(dto, ValidationTypes.listLastRoundsValidationType);
    var season = seasonService.findSeason(dto.season());
    var rounds = roundRepository.listLastRoundsBySeason(season.getName(), dto.qty());
    var fullRounds = transformRounds(rounds, season.getName());
    return new FoundLastRounds(season.getName(), fullRounds);
  }

  public List<FullRound> findAllRounds(String seasonName) {
    var season = seasonService.findSeason(seasonName);
    var rounds = roundRepository.listRoundsBySeason(seasonName);
    return transformRounds(rounds, season.getName());
  }

  public ListRoundsForPlayerDtoOut roundsForPlayer(ListRoundsForPlayerDtoIn dtoIn) {
    Validator.validate(dtoIn, ValidationTypes.listRoundsForPlayerDtoType);
    List<Round> rounds;
    if (nonNull(dtoIn.season())) {
      rounds = roundRepository.listRoundsBySeason(dtoIn.season()).stream()
          .filter(r -> List.of(r.getWinner1(), r.getWinner2(), r.getLoser1(), r.getLoser2()).contains(dtoIn.player()))
          .collect(toList());
    } else {
      rounds = roundRepository.listAllRoundsByPlayer(dtoIn.player(), dtoIn.onlyShutout());
    }
    List<Round> filteredRounds = rounds.stream()
        .filter(r -> !dtoIn.onlyShutout() || r.isShutout())
        .collect(toList());

    int size = filteredRounds.size();
    List<FullRound> result = dtoIn.includeRounds()
        ? filteredRounds.stream().map(FullRound::fromDomain).collect(toList())
        : Collections.emptyList();

    return new ListRoundsForPlayerDtoOut(size, result);
  }

  public IdDto saveRound(AddRoundDto dto) {
    Validator.validate(dto, ValidationTypes.addRoundValidationType);
    userRightsService.checkUserIsAdmin(dto.moderator());
    checkAllPlayersAreDifferent(dto);
    var season = seasonService.findSeasonSafely(SeasonUtils.currentSeason());
    playerService.checkPlayersExist(List.of(dto.w1(), dto.w2(), dto.l1(), dto.l2()));
    var round = new Round(null, dto.w1(), dto.w2(), dto.l1(), dto.l2(), dto.shutout(), season.getName(), DateUtils.now());
    var saveRound = roundRepository.saveRound(round);
    return new IdDto(saveRound.getId());
  }

  public FullRound getRound(GetRoundDto dto) {
    Validator.validate(dto, ValidationTypes.getRoundValidationType);
    Round round = Optional.ofNullable(roundRepository.getById(dto.roundId()))
        .orElseThrow(() -> new RoundNotFoundException(dto.roundId()));
    return FullRound.fromDomain(round);
  }

  private void checkAllPlayersAreDifferent(AddRoundDto dto) {
    List<String> playersList = List.of(dto.w1(), dto.w2(), dto.l1(), dto.l2());
    Set<String> playersSet = new HashSet<>(playersList);
    if (playersList.size() != playersSet.size()) {
      throw new SamePlayersInRoundException();
    }
  }

  private FullRound transformRound(Round r, String s) {
    return new FullRound(
        r.getWinner1(),
        r.getWinner2(),
        r.getLoser1(),
        r.getLoser2(),
        DateUtils.utcToEet(r.getCreated()),
        s, r.isShutout());
  }

  public List<FullRound> transformRounds(List<Round> rounds, String season) {
    return rounds.stream()
        .map(r -> transformRound(r, season))
        .sorted(Comparator.comparing(FullRound::created))
        .collect(toList());
  }
}
