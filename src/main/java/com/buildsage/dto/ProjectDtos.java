package com.buildsage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public final class ProjectDtos {
    private ProjectDtos() {}

    public record CreateProjectRequest(@NotNull UUID teamId, @NotBlank String name, @NotBlank String description) {}

    public record ProjectResponse(UUID id, UUID teamId, String name, String description, Instant createdAt) {}
}
