package com.buildsage.domain;

import com.buildsage.domain.enums.DeploymentStatus;
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
@Table(name = "deployments")
public class Deployment extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private String environment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeploymentStatus status;

    @Column(nullable = false)
    private Instant deployedAt;

    @Column(nullable = false)
    private int riskScore;

    protected Deployment() {}

    public Deployment(Project project, String environment, DeploymentStatus status, Instant deployedAt) {
        this.project = project;
        this.environment = environment;
        this.status = status;
        this.deployedAt = deployedAt;
    }

    public Project getProject() {
        return project;
    }

    public DeploymentStatus getStatus() {
        return status;
    }

    public String getEnvironment() {
        return environment;
    }

    public int getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(int riskScore) {
        this.riskScore = riskScore;
    }
}
