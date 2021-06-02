package com.arbiter.flows.utils;

public class IdGenerator {

  private IdGenerator() {
  }

  public static int msgId() {
    return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
  }

  public static String msgIdString() {
    return String.valueOf(msgId());
  }
}
