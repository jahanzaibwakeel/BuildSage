package com.buildsage.service;

import com.buildsage.api.PageResponse;
import com.buildsage.domain.Project;
import com.buildsage.domain.enums.UserRole;
import com.buildsage.dto.ProjectDtos.CreateProjectRequest;
import com.buildsage.dto.ProjectDtos.ProjectResponse;
import com.buildsage.exception.NotFoundException;
import com.buildsage.repository.ProjectRepository;
import com.buildsage.repository.TeamMemberRepository;
import com.buildsage.repository.TeamRepository;
import com.buildsage.security.CurrentUser;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final AuthorizationService authorizationService;
    private final AuditService auditService;

    public ProjectService(
            ProjectRepository projectRepository,
            TeamRepository teamRepository,
            TeamMemberRepository teamMemberRepository,
            AuthorizationService authorizationService,
            AuditService auditService) {
        this.projectRepository = projectRepository;
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.authorizationService = authorizationService;
        this.auditService = auditService;
    }

    @Transactional
    public ProjectResponse create(CurrentUser user, CreateProjectRequest request) {
        authorizationService.requireWrite(user);
        var team = teamRepository.findById(request.teamId()).orElseThrow(() -> new NotFoundException("Team not found"));
        Project project = projectRepository.save(new Project(team, request.name(), request.description()));
        auditService.record(
                user.id(), "PROJECT_CREATED", "project", project.getId().toString(), project.getName());
        return toResponse(project);
    }

    @Transactional(readOnly = true)
    public PageResponse<ProjectResponse> list(CurrentUser user, String query, Pageable pageable) {
        if (user.role() != UserRole.ADMIN) {
            var memberships = teamMemberRepository.findByUserId(user.id());
            if (memberships.isEmpty()) {
                return new PageResponse<>(java.util.List.of(), pageable.getPageNumber(), pageable.getPageSize(), 0, 0);
            }
            UUID teamId = memberships.get(0).getTeam().getId();
            var scopedPage = (query == null || query.isBlank()
                            ? projectRepository.findByTeamId(teamId, pageable)
                            : projectRepository.findByTeamIdAndNameContainingIgnoreCase(teamId, query, pageable))
                    .map(this::toResponse);
            return PageResponse.from(scopedPage);
        }
        var page = (query == null || query.isBlank()
                        ? projectRepository.findAll(pageable)
                        : projectRepository.findByNameContainingIgnoreCase(query, pageable))
                .map(this::toResponse);
        return PageResponse.from(page);
    }

    public Project get(UUID id) {
        return projectRepository.findById(id).orElseThrow(() -> new NotFoundException("Project not found"));
    }

    private ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getTeam().getId(),
                project.getName(),
                project.getDescription(),
                project.getCreatedAt());
    }
}
