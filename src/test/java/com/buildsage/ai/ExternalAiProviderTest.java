package com.buildsage.ai;

import static org.assertj.core.api.Assertions.assertThat;

import com.buildsage.domain.enums.FailureType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExternalAiProviderTest {
    @Test
    void fallsBackWhenApiKeyIsNotConfigured() {
        ExternalAiProvider provider = new ExternalAiProvider(
                "https://example.invalid", "", "demo-model", new ObjectMapper(), new FailureClassifier());

        AiProvider.AiLogAnalysis analysis = provider.analyzeLogs(List.of("stage=test", "AssertionError: failed"));

        assertThat(analysis.failureType()).isEqualTo(FailureType.TEST_FAILURE);
        assertThat(analysis.summary()).contains("fallback classifier");
    }
}
