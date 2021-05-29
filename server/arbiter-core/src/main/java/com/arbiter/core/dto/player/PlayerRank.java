package com.arbiter.core.dto.player;

public record PlayerRank(String playerSurname,
                         String tid,
                         int rank,
                         String score,
                         int gamesPlayed,
                         int allGames,
                         int allPlayers) {

}
