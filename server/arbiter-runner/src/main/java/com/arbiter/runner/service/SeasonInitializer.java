package com.arbiter.runner.service;

import com.arbiter.core.domain.Season;
import com.arbiter.core.service.SeasonService;
import com.arbiter.core.utils.SeasonUtils;
import org.springframework.stereotype.Service;

@Service
public class SeasonInitializer {

  private final SeasonService seasonService;

  public SeasonInitializer(SeasonService seasonService) {
    this.seasonService = seasonService;
  }

  public Season initSeason() {
    return seasonService.findSeasonSafely(SeasonUtils.currentSeason());
  }


}
