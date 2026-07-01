package com.buildsage.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "teams")
public class Team extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String name;

    protected Team() {}

    public Team(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
