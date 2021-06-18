package com.arbiter.core.dto.round;

import com.arbiter.core.domain.Round;
import java.time.ZonedDateTime;

public record FullRound(String winner1,
                        String winner2,
                        String loser1,
                        String loser2,
                        ZonedDateTime created,
                        String season,
                        boolean shutout) {

  public static FullRound fromDomain(Round r) {
    return new FullRound(r.getWinner1(), r.getWinner2(), r.getLoser1(), r.getLoser2(), r.getCreated(), r.getSeason(), r.isShutout());
  }

}
