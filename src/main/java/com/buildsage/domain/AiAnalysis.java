package com.buildsage.domain;

import com.buildsage.domain.enums.AnalysisStatus;
import com.buildsage.domain.enums.FailureType;
import com.buildsage.domain.enums.ReviewState;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ai_analyses")
public class AiAnalysis extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pipeline_run_id", nullable = false)
    private PipelineRun pipelineRun;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnalysisStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FailureType failureType;

    @Column(nullable = false)
    private String failedStage;

    @Column(nullable = false, length = 8000)
    private String rootCauseSummary;

    @Column(nullable = false)
    private double confidenceScore;

    @Column(nullable = false, length = 8000)
    private String evidenceLines;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewState reviewState;

    @Column(length = 1000)
    private String errorMessage;

    protected AiAnalysis() {}

    public AiAnalysis(PipelineRun pipelineRun) {
        this.pipelineRun = pipelineRun;
        this.status = AnalysisStatus.QUEUED;
        this.failureType = FailureType.UNKNOWN;
        this.failedStage = "unknown";
        this.rootCauseSummary = "Analysis queued.";
        this.confidenceScore = 0.0;
        this.evidenceLines = "";
        this.reviewState = ReviewState.REVIEW_REQUIRED;
    }

    public void markProcessing() {
        this.status = AnalysisStatus.PROCESSING;
    }

    public void complete(
            FailureType failureType,
            String failedStage,
            String rootCauseSummary,
            double confidenceScore,
            String evidenceLines) {
        this.status = AnalysisStatus.COMPLETED;
        this.failureType = failureType;
        this.failedStage = failedStage;
        this.rootCauseSummary = rootCauseSummary;
        this.confidenceScore = confidenceScore;
        this.evidenceLines = evidenceLines;
        this.reviewState = ReviewState.REVIEW_REQUIRED;
    }

    public void fail(String message) {
        this.status = AnalysisStatus.FAILED;
        this.errorMessage = message;
    }

    public AnalysisStatus getStatus() {
        return status;
    }

    public FailureType getFailureType() {
        return failureType;
    }

    public String getFailedStage() {
        return failedStage;
    }

    public String getRootCauseSummary() {
        return rootCauseSummary;
    }

    public double getConfidenceScore() {
        return confidenceScore;
    }

    public String getEvidenceLines() {
        return evidenceLines;
    }

    public ReviewState getReviewState() {
        return reviewState;
    }
}
