package com.arbiter.core.dto.round;

public record ListRoundsForPlayerDtoOut(String player,
                                        String season,
                                        int roundsFoundInSeason,
                                        int shutoutRoundsInSeason,
                                        int roundsTotal,
                                        int winShutoutRoundsTotal) {

}
