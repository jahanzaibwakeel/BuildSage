package com.buildsage.service;

import com.buildsage.ai.AiProvider;
import com.buildsage.domain.Incident;
import com.buildsage.dto.IncidentDtos.CreateIncidentRequest;
import com.buildsage.dto.IncidentDtos.IncidentResponse;
import com.buildsage.exception.NotFoundException;
import com.buildsage.repository.IncidentRepository;
import com.buildsage.security.CurrentUser;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IncidentService {
    private final ProjectService projectService;
    private final IncidentRepository incidentRepository;
    private final AuthorizationService authorizationService;
    private final AiProvider aiProvider;

    public IncidentService(
            ProjectService projectService,
            IncidentRepository incidentRepository,
            AuthorizationService authorizationService,
            AiProvider aiProvider) {
        this.projectService = projectService;
        this.incidentRepository = incidentRepository;
        this.authorizationService = authorizationService;
        this.aiProvider = aiProvider;
    }

    @Transactional
    public IncidentResponse create(CurrentUser user, CreateIncidentRequest request) {
        authorizationService.requireWrite(user);
        authorizationService.requireProjectAccess(user, request.projectId());
        Incident incident = incidentRepository.save(new Incident(
                projectService.get(request.projectId()), request.title(), request.description(), request.severity()));
        return toResponse(incident);
    }

    @Transactional
    public IncidentResponse postmortem(CurrentUser user, UUID incidentId) {
        Incident incident =
                incidentRepository.findById(incidentId).orElseThrow(() -> new NotFoundException("Incident not found"));
        authorizationService.requireProjectAccess(user, incident.getProject().getId());
        incident.setPostmortemDraft(aiProvider.draftPostmortem(
                incident.getTitle(),
                incident.getDescription(),
                incident.getSeverity().name()));
        return toResponse(incident);
    }

    private IncidentResponse toResponse(Incident incident) {
        return new IncidentResponse(
                incident.getId(),
                incident.getProject().getId(),
                incident.getTitle(),
                incident.getSeverity(),
                incident.getPostmortemDraft());
    }
}
