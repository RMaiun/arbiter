package com.arbiter.core.exception;

import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class PlayersNotFoundException extends CataRuntimeException {

  public PlayersNotFoundException(List<String> missedPlayers) {
    super(String.format("Players with names: [%s] were not found", StringUtils.join(missedPlayers, ",")));
  }
}
