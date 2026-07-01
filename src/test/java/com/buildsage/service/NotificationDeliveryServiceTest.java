package com.buildsage.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class NotificationDeliveryServiceTest {
    @Test
    void validatesWebhookSignatureWhenSecretIsConfigured() {
        NotificationDeliveryService service =
                new NotificationDeliveryService(new ObjectMapper(), false, "", "notification-secret");

        String payload = "{\"event\":\"notification.created\"}";
        String signature = "sha256=2c9e651a719cfcbaea49f35bb73661d921987295cca43eb41658441cd429526b";

        assertThat(service.matchesSignature(payload, signature)).isTrue();
        assertThat(service.matchesSignature(payload, "sha256=bad")).isFalse();
    }
}
