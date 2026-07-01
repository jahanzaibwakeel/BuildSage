package com.buildsage.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "pipeline_logs")
public class PipelineLog extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pipeline_run_id", nullable = false)
    private PipelineRun pipelineRun;

    @Column(nullable = false)
    private int lineNumber;

    @Column(nullable = false, length = 4000)
    private String content;

    protected PipelineLog() {}

    public PipelineLog(PipelineRun pipelineRun, int lineNumber, String content) {
        this.pipelineRun = pipelineRun;
        this.lineNumber = lineNumber;
        this.content = content;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getContent() {
        return content;
    }
}
