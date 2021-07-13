package com.arbiter.flows.service;

import com.arbiter.core.domain.Broadcast;
import com.arbiter.core.dto.broadcast.BroadcastDto;
import com.arbiter.core.dto.broadcast.StoreBroadcastDto;
import com.arbiter.core.exception.BroadcastNotFoundException;
import com.arbiter.core.exception.InvalidUserRightsException;
import com.arbiter.core.repository.BroadcastRepository;
import com.arbiter.core.service.PlayerService;
import com.arbiter.core.utils.DateUtils;
import com.arbiter.core.validation.ValidationTypes;
import com.arbiter.core.validation.Validator;
import org.springframework.stereotype.Service;

@Service
public class BroadcastService {

  private final PlayerService playerService;
  private final BroadcastRepository broadcastRepository;

  public BroadcastService(PlayerService playerService, BroadcastRepository broadcastRepository) {
    this.playerService = playerService;
    this.broadcastRepository = broadcastRepository;
  }

  public BroadcastDto storeBroadcast(StoreBroadcastDto dto) {
    Validator.validate(dto, ValidationTypes.storeBroadcastDtoDtoType);
    var user = playerService.findPlayerByTid(dto.author());
    if (!user.isAdmin()){
      throw new InvalidUserRightsException();
    }
    var domain = new Broadcast(dto.author(), dto.message(), DateUtils.now());
    var broadcast = broadcastRepository.saveBroadcast(domain);
    return BroadcastDto.fromDomain(broadcast);
  }

  public BroadcastDto getBroadcast(String id) {
    var broadcast = broadcastRepository.getBroadcast(id);
    return broadcast.map(BroadcastDto::fromDomain)
        .orElseThrow(() -> new BroadcastNotFoundException(id));
  }
}
