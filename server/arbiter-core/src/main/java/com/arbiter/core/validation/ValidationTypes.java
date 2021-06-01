package com.arbiter.core.validation;

import static com.arbiter.core.validation.NumberValidationFunctions.intBetween;
import static com.arbiter.core.validation.StringValidationFunctions.isSeason;
import static com.arbiter.core.validation.StringValidationFunctions.length;
import static com.arbiter.core.validation.StringValidationFunctions.notEmpty;
import static com.arbiter.core.validation.StringValidationFunctions.onlyLetters;
import static com.arbiter.core.validation.StringValidationFunctions.onlyNumbers;
import static com.arbiter.core.validation.ValidationRule.requiredRule;
import static com.arbiter.core.validation.ValidationRule.rule;
import static com.arbiter.core.validation.ValidationSchema.schema;

import com.arbiter.core.dto.player.AddPlayerDto;
import com.arbiter.core.dto.round.AddRoundDto;
import com.arbiter.core.dto.round.FindLastRoundsDto;
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

  ValidationType<String> seasonValidationType = dto ->
      schema().withRule(requiredRule(dto, "season", isSeason()));

  ValidationType<AddPlayerDto> addPlayerValidationType = dto ->
      schema()
          .withRule(rule(dto.tid(), "tid", onlyNumbers()))
          .withRule(requiredRule(dto.surname(), "surname", length(2, 20), onlyLetters()))
          .withRule(rule(dto.moderator(), "moderator", notEmpty()));

  ValidationType<GenerateStatsDocumentDto> generateStatsDocumentValidationType = dto ->
      schema()
          .withRule(requiredRule(dto.season(), "season", isSeason()));

  ValidationType<LinkTidDto> linkTidValidationType = dto ->
      schema()
          .withRule(rule(dto.moderator(), "moderator", notEmpty(), onlyNumbers()))
          .withRule(rule(dto.nameToLink(), "nameToLink", length(2, 20), onlyLetters()))
          .withRule(rule(dto.tid(), "tid", notEmpty(), onlyNumbers()));

  ValidationType<SubscriptionActionDto> subscriptionActionValidationType = dto ->
      schema()
          .withRule(rule(dto.tid(), "moderator", notEmpty(), onlyNumbers()));

}
