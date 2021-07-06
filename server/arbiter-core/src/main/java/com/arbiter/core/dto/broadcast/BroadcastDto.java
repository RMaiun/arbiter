package com.arbiter.core.dto.broadcast;

import com.arbiter.core.domain.Broadcast;
import java.time.ZonedDateTime;

public record BroadcastDto(String id, String author, String message, ZonedDateTime createdAt) {

  public static BroadcastDto fromDomain(Broadcast b) {
    return new BroadcastDto(b.getId(), b.getAuthor(), b.getMessage(), b.getCreatedAt());
  }
}
