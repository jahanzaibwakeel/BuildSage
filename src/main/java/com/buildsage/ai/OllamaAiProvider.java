package com.buildsage.ai;

import com.buildsage.domain.enums.FailureType;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@ConditionalOnProperty(name = "ai.provider", havingValue = "ollama")
public class OllamaAiProvider implements AiProvider {
    private final RestClient restClient;
    private final FailureClassifier classifier;
    private final String model;

    public OllamaAiProvider(
            @Value("${ai.ollama.base-url}") String baseUrl,
            @Value("${ai.ollama.model}") String model,
            FailureClassifier classifier) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
        this.classifier = classifier;
        this.model = model;
    }

    @Override
    public AiLogAnalysis analyzeLogs(List<String> lines) {
        FailureType fallback = classifier.classify(lines);
        String prompt = "Summarize CI/CD failure root cause in 3 sentences:\n"
                + String.join("\n", lines.stream().limit(80).toList());
        String summary;
        try {
            summary = restClient
                    .post()
                    .uri("/api/generate")
                    .body(java.util.Map.of("model", model, "prompt", prompt, "stream", false))
                    .retrieve()
                    .body(String.class);
        } catch (RuntimeException ex) {
            summary = "Local AI provider unavailable; fallback classifier result was used.";
        }
        return new AiLogAnalysis(
                fallback,
                classifier.detectStage(lines),
                summary,
                fallback == FailureType.UNKNOWN ? 0.4 : 0.7,
                lines.stream().limit(5).toList());
    }

    @Override
    public String draftPostmortem(String title, String description, String severity) {
        return "Postmortem draft for " + title + " (" + severity + "): " + description;
    }

    @Override
    public String generateReleaseNotes(List<String> facts) {
        return "## Release Notes\n" + String.join("\n", facts);
    }
}
