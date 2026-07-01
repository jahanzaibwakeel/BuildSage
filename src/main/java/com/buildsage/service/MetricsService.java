package com.buildsage.service;

import com.buildsage.domain.enums.IncidentStatus;
import com.buildsage.domain.enums.PipelineStatus;
import com.buildsage.dto.MetricsDtos.ProjectMetrics;
import com.buildsage.repository.DeploymentRepository;
import com.buildsage.repository.IncidentRepository;
import com.buildsage.repository.PipelineRunRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class MetricsService {
    private final PipelineRunRepository pipelineRunRepository;
    private final IncidentRepository incidentRepository;
    private final DeploymentRepository deploymentRepository;
    private final AuthorizationService authorizationService;

    public MetricsService(
            PipelineRunRepository pipelineRunRepository,
            IncidentRepository incidentRepository,
            DeploymentRepository deploymentRepository,
            AuthorizationService authorizationService) {
        this.pipelineRunRepository = pipelineRunRepository;
        this.incidentRepository = incidentRepository;
        this.deploymentRepository = deploymentRepository;
        this.authorizationService = authorizationService;
    }

    public ProjectMetrics projectMetrics(com.buildsage.security.CurrentUser user, UUID projectId) {
        authorizationService.requireProjectAccess(user, projectId);
        Instant sevenDaysAgo = Instant.now().minus(7, ChronoUnit.DAYS);
        long totalRuns = pipelineRunRepository.countByProjectId(projectId);
        long runsLast7Days = pipelineRunRepository.countByProjectIdAndCreatedAtAfter(projectId, sevenDaysAgo);
        long failed = pipelineRunRepository.countByProjectIdAndStatus(projectId, PipelineStatus.FAILED);
        long failedLast7Days = pipelineRunRepository.countByProjectIdAndStatusAndCreatedAtAfter(
                projectId, PipelineStatus.FAILED, sevenDaysAgo);
        long successful = pipelineRunRepository.countByProjectIdAndStatus(projectId, PipelineStatus.SUCCESS);
        double successRate = totalRuns == 0 ? 0.0 : (double) successful / totalRuns;
        long incidents = incidentRepository.countByProjectIdAndStatus(projectId, IncidentStatus.OPEN);
        var deployments = deploymentRepository.findTop10ByProjectIdOrderByCreatedAtDesc(projectId);
        double risk =
                deployments.stream().mapToInt(d -> d.getRiskScore()).average().orElse(0.0);
        long totalDeployments = deploymentRepository.countByProjectId(projectId);
        long highRiskDeployments = deploymentRepository.countByProjectIdAndRiskScoreGreaterThanEqual(projectId, 70);
        return new ProjectMetrics(
                totalRuns,
                runsLast7Days,
                failed,
                failedLast7Days,
                successRate,
                incidents,
                totalDeployments,
                highRiskDeployments,
                risk);
    }
}
