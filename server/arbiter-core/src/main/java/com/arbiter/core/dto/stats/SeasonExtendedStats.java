package com.arbiter.core.dto.stats;

public record SeasonExtendedStats(String season,
                                  String winner,
                                  Streak currentBestStreak,
                                  Streak currentWorstStreak) {

}
