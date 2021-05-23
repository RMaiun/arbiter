package com.arbiter.core.dto.stats;

import java.util.List;

public record SeasonStatsRows(List<String> headers,
                              List<Integer> totals,
                              List<List<String>> games,
                              List<String> createdDates,
                              Integer roundsPlayed) {

}
