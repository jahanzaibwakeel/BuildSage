package com.buildsage.dto;

import java.time.Instant;
import java.util.UUID;

public final class NotificationDtos {
    private NotificationDtos() {}

    public record NotificationResponse(UUID id, String channel, String message, boolean read, Instant createdAt) {}
}
