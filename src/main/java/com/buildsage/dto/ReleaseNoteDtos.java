package com.buildsage.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public final class ReleaseNoteDtos {
    private ReleaseNoteDtos() {}

    public record GenerateReleaseNotesRequest(@NotBlank String version) {}

    public record ReleaseNoteResponse(UUID id, UUID projectId, String version, String body) {}
}
