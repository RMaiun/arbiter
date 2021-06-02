package com.arbiter.flows.postprocessor;

import com.arbiter.flows.dto.BotInputMessage;
import com.arbiter.flows.utils.Commands;

public interface PostProcessor extends Commands {

  void postProcess(BotInputMessage input, int msgId);
}
