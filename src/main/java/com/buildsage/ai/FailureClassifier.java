package com.buildsage.ai;

import com.buildsage.domain.enums.FailureType;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class FailureClassifier {
    public FailureType classify(List<String> lines) {
        String text = String.join("\n", lines).toLowerCase(Locale.ROOT);
        if (text.contains("test failed")
                || text.contains("assertionerror")
                || text.contains("there were failing tests")) {
            return FailureType.TEST_FAILURE;
        }
        if (text.contains("docker") || text.contains("failed to solve") || text.contains("no such image")) {
            return FailureType.DOCKER_FAILURE;
        }
        if (text.contains("dependency")
                || text.contains("could not resolve")
                || text.contains("npm err!")
                || text.contains("connection reset")) {
            return FailureType.DEPENDENCY_FAILURE;
        }
        if (text.contains("permission denied")
                || text.contains("environment variable")
                || text.contains("secret not found")) {
            return FailureType.ENVIRONMENT_FAILURE;
        }
        if (text.contains("deployment failed")
                || text.contains("helm upgrade failed")
                || text.contains("rollout failed")) {
            return FailureType.DEPLOYMENT_FAILURE;
        }
        if (text.contains("compilation failure")
                || text.contains("cannot find symbol")
                || text.contains("build failed")) {
            return FailureType.BUILD_FAILURE;
        }
        return FailureType.UNKNOWN;
    }

    public String detectStage(List<String> lines) {
        return lines.stream()
                .map(String::toLowerCase)
                .filter(line -> line.contains("stage") || line.contains("job"))
                .findFirst()
                .map(line -> line.length() > 120 ? line.substring(0, 120) : line)
                .orElse("unknown");
    }
}
