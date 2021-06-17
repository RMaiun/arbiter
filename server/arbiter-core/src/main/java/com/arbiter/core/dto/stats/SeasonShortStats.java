package com.arbiter.core.dto.stats;

import java.util.List;

public record SeasonShortStats(String season,
                               List<PlayerStats> playersRating,
                               List<UnrankedStats> unrankedStats,
                               int gamesPlayed,
                               int daysToSeasonEnd,
                               Streak bestStreak,
                               Streak worstStreak) {

}
