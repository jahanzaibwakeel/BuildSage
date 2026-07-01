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

@Entity
@Table(name = "pipeline_jobs")
public class PipelineJob extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pipeline_run_id", nullable = false)
    private PipelineRun pipelineRun;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String stage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PipelineStatus status;

    protected PipelineJob() {}

    public PipelineJob(PipelineRun pipelineRun, String name, String stage, PipelineStatus status) {
        this.pipelineRun = pipelineRun;
        this.name = name;
        this.stage = stage;
        this.status = status;
    }
}
