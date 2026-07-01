package com.buildsage.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "test_failures")
public class TestFailure extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pipeline_run_id", nullable = false)
    private PipelineRun pipelineRun;

    @Column(nullable = false)
    private String testClass;

    @Column(nullable = false)
    private String testName;

    @Column(nullable = false, length = 4000)
    private String errorMessage;

    protected TestFailure() {}

    public TestFailure(PipelineRun pipelineRun, String testClass, String testName, String errorMessage) {
        this.pipelineRun = pipelineRun;
        this.testClass = testClass;
        this.testName = testName;
        this.errorMessage = errorMessage;
    }
}
