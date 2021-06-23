package com.arbiter.core.dto.player;

import java.util.List;

public record ActivatePlayersDto(List<String> players, String moderator) {

}
