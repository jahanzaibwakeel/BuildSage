package com.buildsage.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "repositories")
public class RepositoryMetadata extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private String provider;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String defaultBranch;

    protected RepositoryMetadata() {}

    public RepositoryMetadata(Project project, String provider, String url, String defaultBranch) {
        this.project = project;
        this.provider = provider;
        this.url = url;
        this.defaultBranch = defaultBranch;
    }

    public Project getProject() {
        return project;
    }

    public String getProvider() {
        return provider;
    }

    public String getUrl() {
        return url;
    }

    public String getDefaultBranch() {
        return defaultBranch;
    }
}
