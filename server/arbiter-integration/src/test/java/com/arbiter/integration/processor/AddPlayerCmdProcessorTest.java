package com.arbiter.integration.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.arbiter.core.config.AppProperties;
import com.arbiter.core.domain.Player;
import com.arbiter.core.dto.player.AddPlayerDto;
import com.arbiter.core.exception.InvalidUserRightsException;
import com.arbiter.core.exception.ValidationException;
import com.arbiter.core.repository.PlayerRepository;
import com.arbiter.core.service.PlayerService;
import com.arbiter.core.service.UserRightsService;
import com.arbiter.integration.dto.BotInputMessage;
import com.arbiter.integration.dto.BotOutputMessage;
import com.arbiter.integration.dto.OutputMessage;
import com.arbiter.integration.utils.Commands;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AddPlayerCmdProcessorTest.TestConfig.class})
@TestPropertySource(locations = "classpath:/test.properties")
@DisplayName("AddPlayerCmdProcessor Test")
public class AddPlayerCmdProcessorTest {

  @Autowired
  private PlayerRepository playerRepository;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private AddPlayerCmdProcessor addPlayerCmdProcessor;


  @Test
  public void addPlayerPrivilegedHdsTest() throws JsonProcessingException {
    Player testPlayer = new Player("123", "somePlayer", "xxx", false, false);
    when(playerRepository.getPlayer(any())).thenReturn(Optional.empty());
    when(playerRepository.savePlayer(any(Player.class))).thenReturn(testPlayer);
    Map<String, Object> jsonData = deserializePlayer(new AddPlayerDto("somePlayer", "111", false, "test1"));
    BotInputMessage dtoIn = new BotInputMessage(Commands.ADD_PLAYER_CMD, "12345", "111", "TestUserName", jsonData);
    OutputMessage dtoOut = addPlayerCmdProcessor.process(dtoIn, 1);
    assertNotNull(dtoOut);
    assertFalse(dtoOut.error());
    BotOutputMessage data = dtoOut.data();
    assertNotNull(data);
    assertEquals("12345", data.chatId());
    assertEquals(1, data.msgId());
    assertTrue(data.result().contains("New player was stored with id 123"));
  }

  @Test
  public void addPlayerHdsTest() throws JsonProcessingException {
    Player addedPlayer = new Player("123", "somePlayer", "111111", false, false);
    Player moderator = new Player("456", "moderator", "222222", true, false);
    when(playerRepository.getPlayer(any())).thenReturn(Optional.empty());
    when(playerRepository.savePlayer(any(Player.class))).thenReturn(addedPlayer);
    when(playerRepository.listAll()).thenReturn(List.of(moderator));
    Map<String, Object> jsonData = deserializePlayer(new AddPlayerDto("somePlayer", "111111", false, "222222"));
    BotInputMessage dtoIn = new BotInputMessage(Commands.ADD_PLAYER_CMD, "0", "notUsed", "TestUserName", jsonData);
    OutputMessage dtoOut = addPlayerCmdProcessor.process(dtoIn, 1);
    assertNotNull(dtoOut);
    assertFalse(dtoOut.error());
    BotOutputMessage data = dtoOut.data();
    assertNotNull(data);
    assertEquals("0", data.chatId());
    assertEquals(1, data.msgId());
    assertTrue(data.result().contains("New player was stored with id 123"));
  }

  @Test
  public void addPlayerAuthExceptionTest() throws JsonProcessingException {
    Player addedPlayer = new Player("123", "somePlayer", "111111", false, false);
    Player moderator = new Player("456", "moderator", "222222", false, false);
    when(playerRepository.getPlayer(any())).thenReturn(Optional.empty());
    when(playerRepository.savePlayer(any(Player.class))).thenReturn(addedPlayer);
    when(playerRepository.listAll()).thenReturn(List.of(moderator));
    Map<String, Object> jsonData = deserializePlayer(new AddPlayerDto("somePlayer", "111111", false, "222222"));
    BotInputMessage dtoIn = new BotInputMessage(Commands.ADD_PLAYER_CMD, "0", "notUsed", "TestUserName", jsonData);
    assertThrows(InvalidUserRightsException.class, () -> addPlayerCmdProcessor.process(dtoIn, 1));
  }

  @Test
  public void addPlayerTechnicalValidationFailedTest() throws JsonProcessingException {
    Player addedPlayer = new Player("123", "somePlayer", "111111", false, false);
    Player moderator = new Player("456", "moderator", "222222", false, false);
    when(playerRepository.getPlayer(any())).thenReturn(Optional.empty());
    when(playerRepository.savePlayer(any(Player.class))).thenReturn(addedPlayer);
    when(playerRepository.listAll()).thenReturn(List.of(moderator));
    Map<String, Object> jsonData = deserializePlayer(new AddPlayerDto("somePlayer", "wrongTid", false, "222222"));
    BotInputMessage dtoIn = new BotInputMessage(Commands.ADD_PLAYER_CMD, "0", "notUsed", "TestUserName", jsonData);
    assertThrows(ValidationException.class, () -> addPlayerCmdProcessor.process(dtoIn, 1));
  }

  private Map<String, Object> deserializePlayer(AddPlayerDto dto) throws JsonProcessingException {
    String jsonString = objectMapper.writeValueAsString(dto);
    return objectMapper.readValue(jsonString, new TypeReference<>() {
    });
  }


  @Configuration
  @Import(AppProperties.class)
  static class TestConfig {

    @Bean
    ObjectMapper objectMapper() {
      return new ObjectMapper();
    }

    @Bean
    UserRightsService userRightsService(AppProperties appProperties, PlayerRepository playerRepository) {
      return new UserRightsService(playerRepository, appProperties);
    }

    @Bean
    PlayerRepository playerRepository() {
      return mock(PlayerRepository.class);
    }

    @Bean
    PlayerService playerService(PlayerRepository playerRepository, UserRightsService userRightsService) {
      return new PlayerService(playerRepository, userRightsService);
    }

    @Bean
    AddPlayerCmdProcessor addPlayerCmdProcessor(PlayerService playerService, ObjectMapper objectMapper) {
      return new AddPlayerCmdProcessor(playerService, objectMapper);
    }
  }
}
