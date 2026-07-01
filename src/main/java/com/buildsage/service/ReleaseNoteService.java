package com.buildsage.service;

import com.buildsage.ai.AiProvider;
import com.buildsage.domain.ReleaseNote;
import com.buildsage.dto.ReleaseNoteDtos.GenerateReleaseNotesRequest;
import com.buildsage.dto.ReleaseNoteDtos.ReleaseNoteResponse;
import com.buildsage.repository.DeploymentRepository;
import com.buildsage.repository.PipelineRunRepository;
import com.buildsage.repository.ReleaseNoteRepository;
import com.buildsage.security.CurrentUser;
import java.util.ArrayList;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReleaseNoteService {
    private final ProjectService projectService;
    private final ReleaseNoteRepository releaseNoteRepository;
    private final PipelineRunRepository pipelineRunRepository;
    private final DeploymentRepository deploymentRepository;
    private final AuthorizationService authorizationService;
    private final AiProvider aiProvider;

    public ReleaseNoteService(
            ProjectService projectService,
            ReleaseNoteRepository releaseNoteRepository,
            PipelineRunRepository pipelineRunRepository,
            DeploymentRepository deploymentRepository,
            AuthorizationService authorizationService,
            AiProvider aiProvider) {
        this.projectService = projectService;
        this.releaseNoteRepository = releaseNoteRepository;
        this.pipelineRunRepository = pipelineRunRepository;
        this.deploymentRepository = deploymentRepository;
        this.authorizationService = authorizationService;
        this.aiProvider = aiProvider;
    }

    @Transactional
    public ReleaseNoteResponse generate(CurrentUser user, UUID projectId, GenerateReleaseNotesRequest request) {
        authorizationService.requireWrite(user);
        authorizationService.requireProjectAccess(user, projectId);
        var project = projectService.get(projectId);
        var facts = new ArrayList<String>();
        facts.add("Project: " + project.getName());
        facts.add("Failed pipeline runs to review: "
                + pipelineRunRepository.countByProjectIdAndStatus(
                        projectId, com.buildsage.domain.enums.PipelineStatus.FAILED));
        deploymentRepository
                .findTop10ByProjectIdOrderByCreatedAtDesc(projectId)
                .forEach(deployment -> facts.add(
                        "Deployment to " + deployment.getEnvironment() + " status " + deployment.getStatus()));
        ReleaseNote note = releaseNoteRepository.save(
                new ReleaseNote(project, request.version(), aiProvider.generateReleaseNotes(facts)));
        return new ReleaseNoteResponse(note.getId(), note.getProject().getId(), note.getVersion(), note.getBody());
    }
}
