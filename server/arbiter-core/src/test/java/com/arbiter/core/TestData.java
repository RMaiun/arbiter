package com.arbiter.core;

import com.arbiter.core.domain.Player;
import com.arbiter.core.domain.Round;
import com.arbiter.core.utils.DateUtils;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface TestData {

  static List<Player> testPlayers() {
    Player p1 = new Player("1L", "player1", "test1", true,false);
    Player p2 = new Player("2L", "player2", null, false,false);
    Player p3 = new Player("3L", "player3", null, false,false);
    Player p4 = new Player("4L", "player4", null, false,false);
    return Arrays.asList(p1, p2, p3, p4);
  }

  static List<Round> testRounds() {
    Round r1 = new Round("1L", "1L", "2L", "3L", "4L",
        false, "1L", DateUtils.now());
    Round r2 = new Round("1L", "1L", "3L", "2L", "4L",
        false, "1L", DateUtils.now());
    Round r3 = new Round("1L", "1L", "4L", "2L", "4L",
        false, "1L",DateUtils.now());
    Round r4 = new Round("1L", "1L", "2L", "3L", "4L",
        false, "1L", DateUtils.now());
    return Arrays.asList(r1, r2, r3, r4);
  }

  static List<Player> players(List<String> surnames) {
    return surnames.stream()
        .map(s -> new Player(surnames.indexOf(s) + "1L", s, null, false,false))
        .collect(Collectors.toList());
  }


}
