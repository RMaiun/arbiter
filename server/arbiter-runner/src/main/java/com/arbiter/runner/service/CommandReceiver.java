package com.arbiter.runner.service;


import static com.arbiter.flows.utils.IdGenerator.msgId;

import com.arbiter.core.config.AppProperties;
import com.arbiter.core.service.UserRightsService;
import com.arbiter.flows.postprocessor.PostProcessor;
import com.arbiter.flows.processor.CommandProcessor;
import com.arbiter.rabbit.dto.OutputMessage;
import com.arbiter.rabbit.service.RabbitSender;
import com.arbiter.runner.exception.InvalidCommandException;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;


public class CommandReceiver implements MessageListener {

  private static final Logger log = LogManager.getLogger(CommandReceiver.class);

  private final MetadataParser metadataParser;
  private final RabbitSender rabbitSender;
  private final List<CommandProcessor> processors;
  private final List<PostProcessor> postProcessors;
  private final UserRightsService userRightsService;
  private final AppProperties appProperties;

  public CommandReceiver(MetadataParser metadataParser, RabbitSender rabbitSender,
      List<CommandProcessor> processors, List<PostProcessor> postProcessors,
      UserRightsService userRightsService, AppProperties appProperties) {
    this.metadataParser = metadataParser;
    this.rabbitSender = rabbitSender;
    this.processors = processors;
    this.postProcessors = postProcessors;
    this.userRightsService = userRightsService;
    this.appProperties = appProperties;
  }

  @Override
  public void onMessage(Message message) {
    var start = System.currentTimeMillis();
    var input = metadataParser.parseCommand(message.getBody());
    try {
      userRightsService.checkUserIsRegistered(input.tid());
      var processor = processors.stream()
          .filter(p -> p.commands().contains(input.cmd()))
          .findAny()
          .orElseThrow(() -> new InvalidCommandException(input.cmd()));
      var processResult = processor.process(input, msgId());
      rabbitSender.send(processResult);
      if (appProperties.notificationsEnabled) {
        postProcessors.stream()
            .filter(p -> p.commands().contains(input.cmd()))
            .findAny()
            .ifPresent(pp -> pp.postProcess(input, msgId()));
      }
    } catch (Throwable err) {
      err.printStackTrace();
      var error = OutputMessage.error(input.chatId(), msgId(), format(err));
      rabbitSender.send(error);
    }
    log.info("/{} was called by {} ({}) [{}ms]", input.cmd(), input.user(), input.tid(), System.currentTimeMillis() - start);
  }

  public String format(Throwable error) {
    return String.format("%sERROR: %s%s", CommandProcessor.PREFIX, error.getMessage(), CommandProcessor.SUFFIX);
  }
}
