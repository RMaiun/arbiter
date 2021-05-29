package com.arbiter.core.dto.dump;

import java.util.Map;

public record ImportDumpDto(long seasons, long players, Map<String, Integer> rounds) {

}
