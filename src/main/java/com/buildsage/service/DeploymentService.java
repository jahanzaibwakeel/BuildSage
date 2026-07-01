package com.buildsage.service;

import com.buildsage.domain.Deployment;
import com.buildsage.dto.DeploymentDtos.CreateDeploymentRequest;
import com.buildsage.dto.DeploymentDtos.DeploymentResponse;
import com.buildsage.exception.NotFoundException;
import com.buildsage.repository.DeploymentRepository;
import com.buildsage.security.CurrentUser;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeploymentService {
    private final ProjectService projectService;
    private final DeploymentRepository deploymentRepository;
    private final DeploymentRiskScorer riskScorer;
    private final AuthorizationService authorizationService;

    public DeploymentService(
            ProjectService projectService,
            DeploymentRepository deploymentRepository,
            DeploymentRiskScorer riskScorer,
            AuthorizationService authorizationService) {
        this.projectService = projectService;
        this.deploymentRepository = deploymentRepository;
        this.riskScorer = riskScorer;
        this.authorizationService = authorizationService;
    }

    @Transactional
    public DeploymentResponse create(CurrentUser user, UUID projectId, CreateDeploymentRequest request) {
        authorizationService.requireWrite(user);
        authorizationService.requireProjectAccess(user, projectId);
        Deployment deployment = deploymentRepository.save(new Deployment(
                projectService.get(projectId), request.environment(), request.status(), request.deployedAt()));
        return toResponse(deployment);
    }

    @Transactional
    public DeploymentResponse riskScore(CurrentUser user, UUID deploymentId) {
        Deployment deployment = deploymentRepository
                .findById(deploymentId)
                .orElseThrow(() -> new NotFoundException("Deployment not found"));
        authorizationService.requireProjectAccess(user, deployment.getProject().getId());
        deployment.setRiskScore(riskScorer.score(deployment));
        return toResponse(deployment);
    }

    private DeploymentResponse toResponse(Deployment deployment) {
        return new DeploymentResponse(
                deployment.getId(),
                deployment.getProject().getId(),
                deployment.getEnvironment(),
                deployment.getStatus(),
                deployment.getRiskScore());
    }
}
