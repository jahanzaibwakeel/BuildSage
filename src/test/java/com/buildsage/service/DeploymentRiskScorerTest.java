package com.buildsage.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.buildsage.domain.Deployment;
import com.buildsage.domain.Project;
import com.buildsage.domain.Team;
import com.buildsage.domain.enums.DeploymentStatus;
import com.buildsage.domain.enums.IncidentStatus;
import com.buildsage.domain.enums.PipelineStatus;
import com.buildsage.repository.IncidentRepository;
import com.buildsage.repository.PipelineRunRepository;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DeploymentRiskScorerTest {
    @Test
    void scoresProductionFailedDeploymentAsHighRisk() throws Exception {
        PipelineRunRepository runs = mock(PipelineRunRepository.class);
        IncidentRepository incidents = mock(IncidentRepository.class);
        Project project = new Project(new Team("Platform"), "API", "demo");
        setId(project, UUID.randomUUID());
        Deployment deployment = new Deployment(project, "production", DeploymentStatus.FAILED, Instant.now());

        when(runs.countByProjectIdAndStatus(project.getId(), PipelineStatus.FAILED))
                .thenReturn(3L);
        when(incidents.countByProjectIdAndStatus(project.getId(), IncidentStatus.OPEN))
                .thenReturn(1L);

        assertThat(new DeploymentRiskScorer(runs, incidents).score(deployment)).isGreaterThanOrEqualTo(80);
    }

    private static void setId(Object entity, UUID id) throws Exception {
        Field field = entity.getClass().getSuperclass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(entity, id);
    }
}
