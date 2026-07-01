package com.buildsage.service;

import com.buildsage.domain.Deployment;
import com.buildsage.domain.enums.DeploymentStatus;
import com.buildsage.domain.enums.PipelineStatus;
import com.buildsage.repository.IncidentRepository;
import com.buildsage.repository.PipelineRunRepository;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class DeploymentRiskScorer {
    private final PipelineRunRepository pipelineRunRepository;
    private final IncidentRepository incidentRepository;

    public DeploymentRiskScorer(PipelineRunRepository pipelineRunRepository, IncidentRepository incidentRepository) {
        this.pipelineRunRepository = pipelineRunRepository;
        this.incidentRepository = incidentRepository;
    }

    public int score(Deployment deployment) {
        UUID projectId = deployment.getProject().getId();
        int score = 20;
        score += pipelineRunRepository.countByProjectIdAndStatus(projectId, PipelineStatus.FAILED) * 8;
        score += incidentRepository.countByProjectIdAndStatus(projectId, com.buildsage.domain.enums.IncidentStatus.OPEN)
                * 15;
        if ("production".equalsIgnoreCase(deployment.getEnvironment())) {
            score += 20;
        }
        if (deployment.getStatus() == DeploymentStatus.FAILED
                || deployment.getStatus() == DeploymentStatus.ROLLED_BACK) {
            score += 25;
        }
        return Math.max(0, Math.min(score, 100));
    }
}
