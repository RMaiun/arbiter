package com.arbiter.core.dto.round;

import java.time.ZonedDateTime;

public record FullRound(String winner1,
                        String winner2,
                        String loser1,
                        String loser2,
                        ZonedDateTime created,
                        String season,
                        boolean shutout) {

}
