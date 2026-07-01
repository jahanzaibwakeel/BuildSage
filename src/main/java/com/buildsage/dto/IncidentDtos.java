package com.buildsage.dto;

import com.buildsage.domain.enums.IncidentSeverity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public final class IncidentDtos {
    private IncidentDtos() {}

    public record CreateIncidentRequest(
            @NotNull UUID projectId,
            @NotBlank String title,
            @NotBlank String description,
            @NotNull IncidentSeverity severity) {}

    public record IncidentResponse(
            UUID id, UUID projectId, String title, IncidentSeverity severity, String postmortemDraft) {}
}
