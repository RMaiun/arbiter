package com.arbiter.core.service;

import com.arbiter.core.domain.Season;
import com.arbiter.core.exception.SeasonNotFoundException;
import com.arbiter.core.repository.SeasonRepository;
import com.arbiter.core.utils.DateUtils;
import com.arbiter.core.utils.SeasonUtils;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class SeasonService {

  public static final Logger log = LogManager.getLogger(SeasonService.class);

  private final SeasonRepository seasonRepository;

  public SeasonService(SeasonRepository seasonRepository) {
    this.seasonRepository = seasonRepository;
  }

  public Season findSeason(String season) {
    return seasonRepository.getSeason(season).orElseThrow(() -> new SeasonNotFoundException(season));
  }

  public Season findSeasonSafely(String season) {
    return seasonRepository.getSeason(season)
        .orElseGet(() -> prepareAbsentSeason(season));
  }

  public Optional<Season> findSeasonWithoutNotifications() {
    return seasonRepository.findFirstSeasonWithoutNotification();
  }

  public void ackSendFinalNotifications() {
    var maybeSeason = seasonRepository.findFirstSeasonWithoutNotification();
    maybeSeason.ifPresent(s -> seasonRepository.updateSeason(new Season(s.getId(), s.getName(), DateUtils.now())));
  }

  public Season prepareAbsentSeason(String expected) {
    log.info("Prepare absent season where current: {} and expected: {}", SeasonUtils.currentSeason(), expected);
    if (SeasonUtils.currentSeason().equals(expected)) {
      log.info("Creating new season {}", expected);
      return seasonRepository.saveSeason(Season.of(expected));
    } else {
      log.warn("Expected season ({}) is not the current one ({})", expected, SeasonUtils.currentSeason());
      throw new SeasonNotFoundException(expected);
    }
  }
}
