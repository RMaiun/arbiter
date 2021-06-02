package com.arbiter.flows.processor;

import com.arbiter.flows.dto.BotInputMessage;
import com.arbiter.flows.dto.OutputMessage;
import com.arbiter.flows.utils.Commands;
import java.util.List;

public interface CommandProcessor extends Commands {

  OutputMessage process(BotInputMessage input, int msgId);

  List<String> commands();
}
