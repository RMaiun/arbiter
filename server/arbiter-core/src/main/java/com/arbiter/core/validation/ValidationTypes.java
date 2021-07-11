package com.arbiter.core.validation;

import static com.arbiter.core.validation.ValidationRule.requiredRule;
import static com.arbiter.core.validation.ValidationRule.rule;
import static com.arbiter.core.validation.ValidationSchema.schema;
import static com.arbiter.core.validation.functions.NumberValidationFunctions.intBetween;
import static com.arbiter.core.validation.functions.StringValidationFunctions.isSeason;
import static com.arbiter.core.validation.functions.StringValidationFunctions.length;
import static com.arbiter.core.validation.functions.StringValidationFunctions.notEmpty;
import static com.arbiter.core.validation.functions.StringValidationFunctions.onlyLetters;
import static com.arbiter.core.validation.functions.StringValidationFunctions.onlyNumbers;

import com.arbiter.core.dto.broadcast.StoreBroadcastDto;
import com.arbiter.core.dto.player.AddAchievementDtoIn;
import com.arbiter.core.dto.player.AddPlayerDto;
import com.arbiter.core.dto.round.AddRoundDto;
import com.arbiter.core.dto.round.FindLastRoundsDto;
import com.arbiter.core.dto.round.GetRoundDto;
import com.arbiter.core.dto.round.ListRoundsForPlayerDtoIn;
import com.arbiter.core.dto.stats.GenerateStatsDocumentDto;
import com.arbiter.core.dto.subscription.LinkTidDto;
import com.arbiter.core.dto.subscription.SubscriptionActionDto;

public interface ValidationTypes {

  ValidationType<FindLastRoundsDto> listLastRoundsValidationType = dto ->
      schema()
          .withRule(requiredRule(dto.season(), "season", isSeason()))
          .withRule(requiredRule(dto.qty(), "qty", intBetween(1, 10_000)));

  ValidationType<AddRoundDto> addRoundValidationType = dto ->
      schema()
          .withRule(requiredRule(dto.w1(), "w1", notEmpty(), onlyLetters()))
          .withRule(requiredRule(dto.w2(), "w2", notEmpty(), onlyLetters()))
          .withRule(requiredRule(dto.l1(), "l1", notEmpty(), onlyLetters()))
          .withRule(requiredRule(dto.l2(), "l2", notEmpty(), onlyLetters()))
          .withRule(rule(dto.moderator(), "moderator", notEmpty()));

  ValidationType<GetRoundDto> getRoundValidationType = dto ->
      schema()
          .withRule(requiredRule(dto.roundId(), "roundId", notEmpty()));

  ValidationType<String> seasonValidationType = dto ->
      schema().withRule(requiredRule(dto, "season", isSeason()));

  ValidationType<AddPlayerDto> addPlayerValidationType = dto ->
      schema()
          .withRule(rule(dto.tid(), "tid", onlyNumbers()))
          .withRule(requiredRule(dto.surname(), "surname", length(2, 20), onlyLetters()))
          .withRule(requiredRule(dto.moderator(), "moderator", notEmpty()));

  ValidationType<GenerateStatsDocumentDto> generateStatsDocumentValidationType = dto ->
      schema()
          .withRule(requiredRule(dto.season(), "season", isSeason()));

  ValidationType<LinkTidDto> linkTidValidationType = dto ->
      schema()
          .withRule(requiredRule(dto.moderator(), "moderator", notEmpty(), onlyNumbers()))
          .withRule(requiredRule(dto.nameToLink(), "nameToLink", length(2, 20), onlyLetters()))
          .withRule(requiredRule(dto.tid(), "tid", notEmpty(), onlyNumbers()));

  ValidationType<SubscriptionActionDto> subscriptionActionValidationType = dto ->
      schema()
          .withRule(requiredRule(dto.tid(), "moderator", notEmpty(), onlyNumbers()));

  ValidationType<AddAchievementDtoIn> addAchievementDtoType = dto ->
      schema()
          .withRule(requiredRule(dto.playerName(), "playerName", notEmpty(), onlyLetters()));

  ValidationType<ListRoundsForPlayerDtoIn> listRoundsForPlayerDtoType = dto ->
      schema()
          .withRule(requiredRule(dto.player(), "player", notEmpty(), onlyLetters()))
          .withRule(requiredRule(dto.season(), "season", notEmpty(), isSeason()));

  ValidationType<StoreBroadcastDto> storeBroadcastDtoDtoType = dto ->
      schema()
          .withRule(requiredRule(dto.author(), "author", notEmpty(), onlyNumbers()))
          .withRule(requiredRule(dto.message(), "message", notEmpty(), length(2, 500)));
}
