package com.buildsage.dto;

import com.buildsage.domain.enums.AnalysisStatus;
import com.buildsage.domain.enums.FailureType;
import com.buildsage.domain.enums.PipelineStatus;
import com.buildsage.domain.enums.ReviewState;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class PipelineDtos {
    private PipelineDtos() {}

    public record JobRequest(@NotBlank String name, @NotBlank String stage, @NotNull PipelineStatus status) {}

    public record CreatePipelineRunRequest(
            @NotBlank String externalId,
            @NotBlank String branch,
            @NotBlank String commitSha,
            @NotNull PipelineStatus status,
            @NotNull Instant startedAt,
            Instant finishedAt,
            @Valid List<JobRequest> jobs,
            @NotEmpty @Size(max = 2000) List<@NotBlank @Size(max = 4000) String> logs) {}

    public record PipelineRunResponse(
            UUID id,
            UUID projectId,
            String externalId,
            String branch,
            String commitSha,
            PipelineStatus status,
            Instant createdAt) {}

    public record LogLineResponse(int lineNumber, String content) {}

    public record AnalysisResponse(
            UUID id,
            AnalysisStatus status,
            FailureType failureType,
            String failedStage,
            String rootCauseSummary,
            double confidenceScore,
            String evidenceLines,
            ReviewState reviewState) {}
}
