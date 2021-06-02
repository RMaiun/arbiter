package com.arbiter.flows.utils;

import java.util.List;

public interface Commands extends Constants {

  String ADD_PLAYER_CMD = "addPlayer";
  String ADD_ROUND_CMD = "addRound";
  String FIND_LAST_ROUNDS_CMD = "findLastRounds";
  String LINK_TID_CMD = "linkTid";
  String LIST_PLAYERS_CMD = "listPlayers";
  String SHORT_STATS_CMD = "shortStats";
  String SUBSCRIBE_CMD = "subscribe";
  String UNSUBSCRIBE_CMD = "unsubscribe";
  String STORE_LOG_CMD = "storeLog";

  List<String> commands();
}
