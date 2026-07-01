package com.buildsage.domain;

import com.buildsage.domain.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "team_members")
public class TeamMember extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    protected TeamMember() {}

    public TeamMember(Team team, User user, UserRole role) {
        this.team = team;
        this.user = user;
        this.role = role;
    }

    public Team getTeam() {
        return team;
    }

    public User getUser() {
        return user;
    }

    public UserRole getRole() {
        return role;
    }
}
