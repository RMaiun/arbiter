package com.arbiter.core.dto.stats;

import java.util.List;

public record SeasonShortStats(String season,
                               List<PlayerStats> playersRating,
                               int gamesPlayed,
                               int daysToSeasonEnd,
                               Streak bestStreak,
                               Streak worstStreak) {

}
