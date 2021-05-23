package com.arbiter.core.dto.subscription;

import java.time.ZonedDateTime;

public record SubscriptionResultDto(String subscribedSurname,
                                    String subscribedTid,
                                    ZonedDateTime createdDate,
                                    boolean notificationsEnabled) {

}
