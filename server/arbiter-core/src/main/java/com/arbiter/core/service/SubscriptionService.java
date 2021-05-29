package com.arbiter.core.service;


import static com.arbiter.core.utils.DateUtils.now;

import com.arbiter.core.dto.subscription.LinkTidDto;
import com.arbiter.core.dto.subscription.SubscriptionActionDto;
import com.arbiter.core.dto.subscription.SubscriptionResultDto;
import com.arbiter.core.validation.ValidationTypes;
import com.arbiter.core.validation.Validator;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {

  private final UserRightsService userRightsService;
  private final PlayerService playerService;

  public SubscriptionService(UserRightsService userRightsService, PlayerService playerService) {
    this.userRightsService = userRightsService;
    this.playerService = playerService;
  }

  public SubscriptionResultDto linkTidForPlayer(LinkTidDto dto) {
    Validator.validate(dto, ValidationTypes.linkTidValidationType);
    userRightsService.checkUserIsAdmin(dto.moderator());
    playerService.enableNotifications(dto.nameToLink(), dto.tid());
    return new SubscriptionResultDto(dto.nameToLink(), dto.tid(), now(), true);
  }

  public SubscriptionResultDto updateSubscriptionsStatus(SubscriptionActionDto dto) {
    Validator.validate(dto, ValidationTypes.subscriptionActionValidationType);
    var player = playerService.findPlayerByTid(dto.tid());
    player.setNotificationsEnabled(dto.enableSubscriptions());
    var updatedPlayer = playerService.updatePlayer(player);
    return new SubscriptionResultDto(updatedPlayer.getSurname(), updatedPlayer.getTid(), now(), dto.enableSubscriptions());
  }

}
