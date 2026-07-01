package com.buildsage.ai;

import com.buildsage.domain.enums.FailureType;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "ai.provider", havingValue = "mock", matchIfMissing = true)
public class MockAiProvider implements AiProvider {
    private final FailureClassifier classifier;

    public MockAiProvider(FailureClassifier classifier) {
        this.classifier = classifier;
    }

    @Override
    public AiLogAnalysis analyzeLogs(List<String> lines) {
        FailureType type = classifier.classify(lines);
        String stage = classifier.detectStage(lines);
        List<String> evidence = lines.stream()
                .filter(line ->
                        line.toLowerCase().matches(".*(failed|error|exception|denied|docker|resolve|rollout).*"))
                .limit(5)
                .toList();
        String summary =
                switch (type) {
                    case TEST_FAILURE ->
                        "Automated tests failed. Review failing assertions and recent changes around the reported test cases.";
                    case DOCKER_FAILURE ->
                        "Container build or image resolution failed. Check Dockerfile layers, registry access, and build context.";
                    case DEPENDENCY_FAILURE -> "A dependency resolution or network failure interrupted the pipeline.";
                    case ENVIRONMENT_FAILURE ->
                        "The pipeline appears blocked by missing environment configuration or permissions.";
                    case DEPLOYMENT_FAILURE -> "Deployment orchestration reported a rollout failure.";
                    case BUILD_FAILURE -> "The application build failed before packaging completed.";
                    case UNKNOWN -> "The failure pattern is not confidently classified and needs human review.";
                };
        double confidence = type == FailureType.UNKNOWN ? 0.35 : 0.82;
        return new AiLogAnalysis(type, stage, summary, confidence, evidence);
    }

    @Override
    public String draftPostmortem(String title, String description, String severity) {
        return "## Summary\n" + title + "\n\n## Impact\nSeverity " + severity + " incident: " + description
                + "\n\n## Suspected Root Cause\nGenerated from incident context; requires engineer review.\n\n## Corrective Actions\n- Add regression coverage\n- Improve alerts\n- Review deployment guardrails";
    }

    @Override
    public String generateReleaseNotes(List<String> facts) {
        return "## Release Notes\n"
                + String.join("\n", facts.stream().map(fact -> "- " + fact).toList());
    }
}
