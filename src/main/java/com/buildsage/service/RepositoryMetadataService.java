package com.buildsage.service;

import com.buildsage.api.PageResponse;
import com.buildsage.domain.RepositoryMetadata;
import com.buildsage.dto.RepositoryDtos.CreateRepositoryRequest;
import com.buildsage.dto.RepositoryDtos.RepositoryResponse;
import com.buildsage.exception.NotFoundException;
import com.buildsage.repository.RepositoryMetadataRepository;
import com.buildsage.security.CurrentUser;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RepositoryMetadataService {
    private final ProjectService projectService;
    private final RepositoryMetadataRepository repositoryMetadataRepository;
    private final AuthorizationService authorizationService;
    private final AuditService auditService;

    public RepositoryMetadataService(
            ProjectService projectService,
            RepositoryMetadataRepository repositoryMetadataRepository,
            AuthorizationService authorizationService,
            AuditService auditService) {
        this.projectService = projectService;
        this.repositoryMetadataRepository = repositoryMetadataRepository;
        this.authorizationService = authorizationService;
        this.auditService = auditService;
    }

    @Transactional
    public RepositoryResponse create(CurrentUser user, UUID projectId, CreateRepositoryRequest request) {
        authorizationService.requireWrite(user);
        authorizationService.requireProjectAccess(user, projectId);
        RepositoryMetadata metadata = repositoryMetadataRepository.save(new RepositoryMetadata(
                projectService.get(projectId), request.provider(), request.url(), request.defaultBranch()));
        auditService.record(
                user.id(), "REPOSITORY_CREATED", "repository", metadata.getId().toString(), metadata.getUrl());
        return toResponse(metadata);
    }

    @Transactional(readOnly = true)
    public PageResponse<RepositoryResponse> list(CurrentUser user, UUID projectId, Pageable pageable) {
        authorizationService.requireProjectAccess(user, projectId);
        return PageResponse.from(repositoryMetadataRepository
                .findByProjectId(projectId, pageable)
                .map(this::toResponse));
    }

    @Transactional(readOnly = true)
    public RepositoryResponse get(CurrentUser user, UUID id) {
        RepositoryMetadata metadata = repositoryMetadataRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Repository metadata not found"));
        authorizationService.requireProjectAccess(user, metadata.getProject().getId());
        return toResponse(metadata);
    }

    private RepositoryResponse toResponse(RepositoryMetadata metadata) {
        return new RepositoryResponse(
                metadata.getId(),
                metadata.getProject().getId(),
                metadata.getProvider(),
                metadata.getUrl(),
                metadata.getDefaultBranch());
    }
}
