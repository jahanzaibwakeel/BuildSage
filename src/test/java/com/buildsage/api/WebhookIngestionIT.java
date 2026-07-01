package com.buildsage.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = "webhooks.github.secret=test-webhook-secret")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WebhookIngestionIT {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void signedGithubWebhookCanIngestPipelineRunWithoutJwt() throws Exception {
        String payload = objectMapper.writeValueAsString(Map.of(
                "externalId",
                "github-webhook-1",
                "branch",
                "main",
                "commitSha",
                "abc123",
                "status",
                "FAILED",
                "startedAt",
                Instant.now().toString(),
                "logs",
                java.util.List.of("stage=test", "AssertionError in checkout flow")));

        String response = mockMvc.perform(
                        post("/api/webhooks/github/projects/20000000-0000-0000-0000-000000000001/pipeline-runs")
                                .header("X-Hub-Signature-256", signature(payload))
                                .header("X-GitHub-Delivery", "delivery-123")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.idempotencyKey").value("delivery-123"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonNode root = objectMapper.readTree(response);
        assertThat(root.get("data").get("externalId").asText()).isEqualTo("github-webhook-1");
    }

    @Test
    void rejectsInvalidGithubWebhookSignature() throws Exception {
        String payload = objectMapper.writeValueAsString(Map.of(
                "externalId",
                "github-webhook-bad",
                "branch",
                "main",
                "commitSha",
                "abc123",
                "status",
                "FAILED",
                "startedAt",
                Instant.now().toString(),
                "logs",
                java.util.List.of("bad signature")));

        mockMvc.perform(post("/api/webhooks/github/projects/20000000-0000-0000-0000-000000000001/pipeline-runs")
                        .header("X-Hub-Signature-256", "sha256=invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error.code").value("FORBIDDEN"));
    }

    private String signature(String payload) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec("test-webhook-secret".getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return "sha256=" + HexFormat.of().formatHex(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
    }
}
