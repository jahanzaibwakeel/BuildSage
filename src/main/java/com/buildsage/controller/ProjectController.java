package com.buildsage.controller;

import com.buildsage.api.ApiResponse;
import com.buildsage.api.PageResponse;
import com.buildsage.dto.MetricsDtos.ProjectMetrics;
import com.buildsage.dto.ProjectDtos.CreateProjectRequest;
import com.buildsage.dto.ProjectDtos.ProjectResponse;
import com.buildsage.security.CurrentUser;
import com.buildsage.service.MetricsService;
import com.buildsage.service.ProjectService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService projectService;
    private final MetricsService metricsService;

    public ProjectController(ProjectService projectService, MetricsService metricsService) {
        this.projectService = projectService;
        this.metricsService = metricsService;
    }

    @PostMapping
    ApiResponse<ProjectResponse> create(
            @AuthenticationPrincipal CurrentUser user, @Valid @RequestBody CreateProjectRequest request) {
        return ApiResponse.ok(projectService.create(user, request));
    }

    @GetMapping
    ApiResponse<PageResponse<ProjectResponse>> list(
            @AuthenticationPrincipal CurrentUser user,
            @RequestParam(required = false) String query,
            Pageable pageable) {
        return ApiResponse.ok(projectService.list(user, query, pageable));
    }

    @GetMapping("/{id}/metrics")
    ApiResponse<ProjectMetrics> metrics(@AuthenticationPrincipal CurrentUser user, @PathVariable UUID id) {
        return ApiResponse.ok(metricsService.projectMetrics(user, id));
    }
}
