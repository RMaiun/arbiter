package com.arbiter.core.dto.stats;

import java.util.List;

public record SeasonStatsForPlayerDtoOut(String player,
                                         List<SeasonExtendedStats> seasonStats) {

}
