package com.buildsage.service;

import com.buildsage.domain.enums.UserRole;
import com.buildsage.exception.ForbiddenException;
import com.buildsage.repository.ProjectRepository;
import com.buildsage.repository.TeamMemberRepository;
import com.buildsage.security.CurrentUser;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;

    public AuthorizationService(ProjectRepository projectRepository, TeamMemberRepository teamMemberRepository) {
        this.projectRepository = projectRepository;
        this.teamMemberRepository = teamMemberRepository;
    }

    public void requireProjectAccess(CurrentUser user, UUID projectId) {
        if (user.role() == UserRole.ADMIN) {
            return;
        }
        var project = projectRepository
                .findById(projectId)
                .orElseThrow(() -> new ForbiddenException("Project access denied"));
        if (!teamMemberRepository.existsByTeamIdAndUserId(project.getTeam().getId(), user.id())) {
            throw new ForbiddenException("Project access denied");
        }
    }

    public void requireWrite(CurrentUser user) {
        if (user.role() == UserRole.VIEWER) {
            throw new ForbiddenException("Write access requires ADMIN or DEVELOPER role");
        }
    }
}
