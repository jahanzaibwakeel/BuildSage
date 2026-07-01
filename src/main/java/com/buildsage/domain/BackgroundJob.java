package com.buildsage.domain;

import com.buildsage.domain.enums.AnalysisStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "background_jobs")
public class BackgroundJob extends BaseEntity {
    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private UUID targetId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnalysisStatus status;

    @Column(length = 1000)
    private String message;

    protected BackgroundJob() {}

    public BackgroundJob(String type, UUID targetId) {
        this.type = type;
        this.targetId = targetId;
        this.status = AnalysisStatus.QUEUED;
    }

    public void mark(AnalysisStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
