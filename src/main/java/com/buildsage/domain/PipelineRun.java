package com.buildsage.domain;

import com.buildsage.domain.enums.PipelineStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "pipeline_runs")
public class PipelineRun extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private String externalId;

    @Column
    private String idempotencyKey;

    @Column(nullable = false)
    private String branch;

    @Column(nullable = false)
    private String commitSha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PipelineStatus status;

    @Column(nullable = false)
    private Instant startedAt;

    private Instant finishedAt;

    @Column(length = 1000)
    private String logArchiveUri;

    @Column(length = 64)
    private String logDigestSha256;

    @Column(nullable = false)
    private int logLineCount;

    protected PipelineRun() {}

    public PipelineRun(
            Project project,
            String externalId,
            String idempotencyKey,
            String branch,
            String commitSha,
            PipelineStatus status,
            Instant startedAt,
            Instant finishedAt,
            String logArchiveUri,
            String logDigestSha256,
            int logLineCount) {
        this.project = project;
        this.externalId = externalId;
        this.idempotencyKey = idempotencyKey;
        this.branch = branch;
        this.commitSha = commitSha;
        this.status = status;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.logArchiveUri = logArchiveUri;
        this.logDigestSha256 = logDigestSha256;
        this.logLineCount = logLineCount;
    }

    public Project getProject() {
        return project;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public String getBranch() {
        return branch;
    }

    public String getCommitSha() {
        return commitSha;
    }

    public PipelineStatus getStatus() {
        return status;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getFinishedAt() {
        return finishedAt;
    }

    public String getLogArchiveUri() {
        return logArchiveUri;
    }

    public String getLogDigestSha256() {
        return logDigestSha256;
    }

    public int getLogLineCount() {
        return logLineCount;
    }
}
