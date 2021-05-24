package com.arbiter.core.service;

import com.arbiter.core.TestConfig;
import com.arbiter.core.TestData;
import com.arbiter.core.exception.InvalidUserRightsException;
import com.arbiter.core.repository.PlayerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:/test.properties")
@ContextConfiguration(classes = {TestConfig.class})
@DisplayName("UserRightsService Test")
public class UserRightsServiceTest {

  @Autowired
  private UserRightsService userRightsService;
  @Autowired
  private PlayerRepository playerRepository;

  @AfterEach
  public void clearDb() {
    playerRepository.removeByTid(TestData.testPlayers().get(0).getTid());
  }

  @Test
  @DisplayName("UserRightsService successfully processed privileged user")
  public void privilegedUserHdsTest() {
    Assertions.assertDoesNotThrow(() -> userRightsService.checkUserIsAdmin("testId"),
        "Privileged user should be valid");
  }

  @Test
  @DisplayName("UserRightsService successfully processed user")
  public void userHdsTest() {
    var p = TestData.testPlayers().get(0);
    var stored = playerRepository.savePlayer(p);
    Assertions.assertDoesNotThrow(() -> userRightsService.checkUserIsAdmin(stored.getTid()),
        "Privileged user should be valid");

  }

  @Test
  @DisplayName("UserRightsService should throw InvalidUserRightsException")
  public void userHasNoPermissionsTest() {
    Assertions.assertThrows(InvalidUserRightsException.class,
        () -> userRightsService.checkUserIsAdmin("bla"));
  }
}
