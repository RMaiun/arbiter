package com.arbiter.flows.processor;

import com.arbiter.rabbit.dto.BotInputMessage;
import com.arbiter.rabbit.dto.OutputMessage;
import com.arbiter.flows.utils.Commands;
import java.util.List;

public interface CommandProcessor extends Commands {

  OutputMessage process(BotInputMessage input, int msgId);

  List<String> commands();
}
