package com.buildsage.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "projects")
public class Project extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    protected Project() {}

    public Project(Team team, String name, String description) {
        this.team = team;
        this.name = name;
        this.description = description;
    }

    public Team getTeam() {
        return team;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
