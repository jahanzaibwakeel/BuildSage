package com.buildsage.domain;

import com.buildsage.domain.enums.IncidentSeverity;
import com.buildsage.domain.enums.IncidentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "incidents")
public class Incident extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 4000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncidentSeverity severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncidentStatus status;

    @Column(length = 8000)
    private String postmortemDraft;

    protected Incident() {}

    public Incident(Project project, String title, String description, IncidentSeverity severity) {
        this.project = project;
        this.title = title;
        this.description = description;
        this.severity = severity;
        this.status = IncidentStatus.OPEN;
    }

    public Project getProject() {
        return project;
    }

    public String getTitle() {
        return title;
    }

    public IncidentSeverity getSeverity() {
        return severity;
    }

    public String getDescription() {
        return description;
    }

    public String getPostmortemDraft() {
        return postmortemDraft;
    }

    public void setPostmortemDraft(String postmortemDraft) {
        this.postmortemDraft = postmortemDraft;
    }
}
