package com.buildsage.controller;

import com.buildsage.api.ApiResponse;
import com.buildsage.api.PageResponse;
import com.buildsage.dto.RepositoryDtos.CreateRepositoryRequest;
import com.buildsage.dto.RepositoryDtos.RepositoryResponse;
import com.buildsage.security.CurrentUser;
import com.buildsage.service.RepositoryMetadataService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RepositoryMetadataController {
    private final RepositoryMetadataService repositoryMetadataService;

    public RepositoryMetadataController(RepositoryMetadataService repositoryMetadataService) {
        this.repositoryMetadataService = repositoryMetadataService;
    }

    @PostMapping("/projects/{projectId}/repositories")
    ApiResponse<RepositoryResponse> create(
            @AuthenticationPrincipal CurrentUser user,
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateRepositoryRequest request) {
        return ApiResponse.ok(repositoryMetadataService.create(user, projectId, request));
    }

    @GetMapping("/projects/{projectId}/repositories")
    ApiResponse<PageResponse<RepositoryResponse>> list(
            @AuthenticationPrincipal CurrentUser user, @PathVariable UUID projectId, Pageable pageable) {
        return ApiResponse.ok(repositoryMetadataService.list(user, projectId, pageable));
    }

    @GetMapping("/repositories/{id}")
    ApiResponse<RepositoryResponse> get(@AuthenticationPrincipal CurrentUser user, @PathVariable UUID id) {
        return ApiResponse.ok(repositoryMetadataService.get(user, id));
    }
}
