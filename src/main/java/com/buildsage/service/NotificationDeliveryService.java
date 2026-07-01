package com.buildsage.service;

import com.buildsage.domain.Notification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class NotificationDeliveryService {
    private static final Logger log = LoggerFactory.getLogger(NotificationDeliveryService.class);

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final boolean enabled;
    private final String webhookUrl;
    private final String webhookSecret;

    public NotificationDeliveryService(
            ObjectMapper objectMapper,
            @Value("${notifications.webhook.enabled:false}") boolean enabled,
            @Value("${notifications.webhook.url:}") String webhookUrl,
            @Value("${notifications.webhook.secret:}") String webhookSecret) {
        this.restClient = RestClient.create();
        this.objectMapper = objectMapper;
        this.enabled = enabled;
        this.webhookUrl = webhookUrl;
        this.webhookSecret = webhookSecret;
    }

    public void deliver(Notification notification) {
        if (!enabled || webhookUrl == null || webhookUrl.isBlank()) {
            return;
        }
        try {
            String payload = objectMapper.writeValueAsString(payload(notification));
            var request = restClient
                    .post()
                    .uri(webhookUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-BuildSage-Event", "notification.created");
            if (webhookSecret != null && !webhookSecret.isBlank()) {
                request.header("X-BuildSage-Signature-256", "sha256=" + hmacSha256(payload));
            }
            request.body(payload).retrieve().toBodilessEntity();
        } catch (JsonProcessingException | RestClientException ex) {
            log.warn("Notification webhook delivery failed for notification {}", notification.getId(), ex);
        }
    }

    private Map<String, Object> payload(Notification notification) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("event", "notification.created");
        body.put("notificationId", notification.getId());
        body.put("userId", notification.getUserId());
        body.put("channel", notification.getChannel());
        body.put("message", notification.getMessage());
        body.put("createdAt", createdAt(notification));
        return body;
    }

    private String createdAt(Notification notification) {
        Instant createdAt = notification.getCreatedAt();
        return createdAt == null ? null : createdAt.toString();
    }

    private String hmacSha256(String payload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to sign notification webhook", ex);
        }
    }

    public boolean matchesSignature(String payload, String signatureHeader) {
        if (webhookSecret == null || webhookSecret.isBlank() || signatureHeader == null) {
            return false;
        }
        String expected = "sha256=" + hmacSha256(payload);
        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8), signatureHeader.getBytes(StandardCharsets.UTF_8));
    }
}
