package com.buildsage.ai;

import static org.assertj.core.api.Assertions.assertThat;

import com.buildsage.domain.enums.FailureType;
import java.util.List;
import org.junit.jupiter.api.Test;

class FailureClassifierTest {
    private final FailureClassifier classifier = new FailureClassifier();

    @Test
    void classifiesTestFailures() {
        assertThat(classifier.classify(List.of("There were failing tests", "AssertionError")))
                .isEqualTo(FailureType.TEST_FAILURE);
    }

    @Test
    void classifiesDockerFailures() {
        assertThat(classifier.classify(List.of("docker build failed", "failed to solve")))
                .isEqualTo(FailureType.DOCKER_FAILURE);
    }

    @Test
    void fallsBackToUnknown() {
        assertThat(classifier.classify(List.of("pipeline stopped without diagnostic")))
                .isEqualTo(FailureType.UNKNOWN);
    }
}
