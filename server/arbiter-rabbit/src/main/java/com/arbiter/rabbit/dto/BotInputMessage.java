package com.arbiter.rabbit.dto;

import java.util.Map;

public record BotInputMessage(String cmd,
                              String chatId,
                              String tid,
                              String user,
                              Map<String, Object> data) {
}
