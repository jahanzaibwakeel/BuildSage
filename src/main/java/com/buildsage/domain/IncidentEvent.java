package com.buildsage.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "incident_events")
public class IncidentEvent extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_id", nullable = false)
    private Incident incident;

    @Column(nullable = false)
    private Instant occurredAt;

    @Column(nullable = false, length = 4000)
    private String message;

    protected IncidentEvent() {}

    public IncidentEvent(Incident incident, Instant occurredAt, String message) {
        this.incident = incident;
        this.occurredAt = occurredAt;
        this.message = message;
    }
}
