package com.arbiter.core.dto.round;

public record AddRoundDto(String w1,
                          String w2,
                          String l1,
                          String l2,
                          boolean shutout,
                          String moderator) {

}
