package com.buildsage.ai;

import com.buildsage.domain.enums.FailureType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@ConditionalOnProperty(name = "ai.provider", havingValue = "external")
public class ExternalAiProvider implements AiProvider {
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final FailureClassifier classifier;
    private final String model;
    private final String apiKey;

    public ExternalAiProvider(
            @Value("${ai.external.base-url}") String baseUrl,
            @Value("${ai.external.api-key}") String apiKey,
            @Value("${ai.external.model}") String model,
            ObjectMapper objectMapper,
            FailureClassifier classifier) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
        this.objectMapper = objectMapper;
        this.classifier = classifier;
        this.model = model;
        this.apiKey = apiKey;
    }

    @Override
    public AiLogAnalysis analyzeLogs(List<String> lines) {
        FailureType fallback = classifier.classify(lines);
        String prompt = "Analyze this CI/CD log. Return concise root cause and remediation hints.\n"
                + String.join("\n", lines.stream().limit(120).toList());
        String summary = callChatCompletion(prompt, "External AI unavailable; fallback classifier result was used.");
        return new AiLogAnalysis(
                fallback,
                classifier.detectStage(lines),
                summary,
                fallback == FailureType.UNKNOWN ? 0.45 : 0.78,
                lines.stream()
                        .filter(line -> line.toLowerCase().matches(".*(error|failed|exception|denied).*"))
                        .limit(5)
                        .toList());
    }

    @Override
    public String draftPostmortem(String title, String description, String severity) {
        return callChatCompletion(
                "Draft a short incident postmortem for severity " + severity + ". Title: " + title + ". Description: "
                        + description,
                "External AI unavailable; postmortem requires manual drafting.");
    }

    @Override
    public String generateReleaseNotes(List<String> facts) {
        return callChatCompletion(
                "Generate release notes from these facts:\n" + String.join("\n", facts),
                "## Release Notes\n"
                        + String.join(
                                "\n", facts.stream().map(fact -> "- " + fact).toList()));
    }

    private String callChatCompletion(String prompt, String fallback) {
        if (apiKey == null || apiKey.isBlank()) {
            return fallback;
        }
        try {
            String response = restClient
                    .post()
                    .uri("/v1/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .body(Map.of(
                            "model",
                            model,
                            "messages",
                            List.of(Map.of("role", "user", "content", prompt)),
                            "temperature",
                            0.2))
                    .retrieve()
                    .body(String.class);
            JsonNode root = objectMapper.readTree(response);
            return root.path("choices").path(0).path("message").path("content").asText(fallback);
        } catch (RuntimeException | java.io.IOException ex) {
            return fallback;
        }
    }
}
