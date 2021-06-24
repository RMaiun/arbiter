package com.arbiter.core.dto.round;

public record ListRoundsForPlayerDtoIn(String player, String season, boolean includeRounds, boolean onlyShutout) {

}
