package com.arbiter.rabbit.dto;

public record OutputMessage(boolean error, BotOutputMessage data) {

  public static OutputMessage ok(String chatId, int msgId, String result) {
    return new OutputMessage(false, new BotOutputMessage(chatId, msgId, result));
  }

  public static OutputMessage ok(BotOutputMessage botOutputMessage) {
    return new OutputMessage(false, botOutputMessage);
  }

  public static OutputMessage error(String chatId, int msgId, String result) {
    return new OutputMessage(true, new BotOutputMessage(chatId, msgId, result));
  }

  public static OutputMessage error(BotOutputMessage botOutputMessage) {
    return new OutputMessage(true, botOutputMessage);
  }

}
