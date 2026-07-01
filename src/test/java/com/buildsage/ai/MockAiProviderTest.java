package com.buildsage.ai;

import static org.assertj.core.api.Assertions.assertThat;

import com.buildsage.domain.enums.FailureType;
import java.util.List;
import org.junit.jupiter.api.Test;

class MockAiProviderTest {
    @Test
    void returnsReviewableAnalysisResult() {
        MockAiProvider provider = new MockAiProvider(new FailureClassifier());
        var result = provider.analyzeLogs(List.of("stage=test", "test failed: expected true"));

        assertThat(result.failureType()).isEqualTo(FailureType.TEST_FAILURE);
        assertThat(result.confidence()).isGreaterThan(0.5);
        assertThat(result.summary()).contains("tests");
    }
}
