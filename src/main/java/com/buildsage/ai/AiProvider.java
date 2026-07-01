package com.buildsage.ai;

import com.buildsage.domain.enums.FailureType;
import java.util.List;

public interface AiProvider {
    AiLogAnalysis analyzeLogs(List<String> lines);

    String draftPostmortem(String title, String description, String severity);

    String generateReleaseNotes(List<String> facts);

    record AiLogAnalysis(
            FailureType failureType,
            String failedStage,
            String summary,
            double confidence,
            List<String> evidenceLines) {}
}
