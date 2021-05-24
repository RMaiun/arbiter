package com.arbiter.integration.postprocessor;

import com.arbiter.integration.dto.BotInputMessage;
import com.arbiter.integration.dto.OutputMessage;
import com.arbiter.integration.utils.Commands;

public interface PostProcessor extends Commands {

  void postProcess(BotInputMessage input, int msgId);
}
