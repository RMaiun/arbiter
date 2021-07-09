package com.arbiter.core.dto.player;

import java.util.List;

public record AddAchievementDtoIn(String playerName, List<String> achievementCodes) {

}
