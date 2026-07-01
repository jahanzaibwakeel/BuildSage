package com.buildsage.dto;

import com.buildsage.domain.enums.DeploymentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public final class DeploymentDtos {
    private DeploymentDtos() {}

    public record CreateDeploymentRequest(
            @NotBlank String environment, @NotNull DeploymentStatus status, @NotNull Instant deployedAt) {}

    public record DeploymentResponse(
            UUID id, UUID projectId, String environment, DeploymentStatus status, int riskScore) {}
}
