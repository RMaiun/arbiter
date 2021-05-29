package com.arbiter.integration.processor;

import com.arbiter.integration.dto.BotInputMessage;
import com.arbiter.integration.dto.OutputMessage;
import com.arbiter.integration.utils.Commands;
import java.util.List;

public interface CommandProcessor extends Commands {

  OutputMessage process(BotInputMessage input, int msgId);

  List<String> commands();
}
