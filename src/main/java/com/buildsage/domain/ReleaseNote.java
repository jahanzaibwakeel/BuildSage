package com.buildsage.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "release_notes")
public class ReleaseNote extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private String version;

    @Column(nullable = false, length = 8000)
    private String body;

    protected ReleaseNote() {}

    public ReleaseNote(Project project, String version, String body) {
        this.project = project;
        this.version = version;
        this.body = body;
    }

    public Project getProject() {
        return project;
    }

    public String getVersion() {
        return version;
    }

    public String getBody() {
        return body;
    }
}
