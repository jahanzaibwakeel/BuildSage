package com.buildsage.controller;

import com.buildsage.api.ApiResponse;
import com.buildsage.dto.DeploymentDtos.CreateDeploymentRequest;
import com.buildsage.dto.DeploymentDtos.DeploymentResponse;
import com.buildsage.security.CurrentUser;
import com.buildsage.service.DeploymentService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DeploymentController {
    private final DeploymentService deploymentService;

    public DeploymentController(DeploymentService deploymentService) {
        this.deploymentService = deploymentService;
    }

    @PostMapping("/projects/{id}/deployments")
    ApiResponse<DeploymentResponse> create(
            @AuthenticationPrincipal CurrentUser user,
            @PathVariable UUID id,
            @Valid @RequestBody CreateDeploymentRequest request) {
        return ApiResponse.ok(deploymentService.create(user, id, request));
    }

    @PostMapping("/deployments/{id}/risk-score")
    ApiResponse<DeploymentResponse> risk(@AuthenticationPrincipal CurrentUser user, @PathVariable UUID id) {
        return ApiResponse.ok(deploymentService.riskScore(user, id));
    }
}
