package com.buildsage.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import org.hibernate.validator.constraints.URL;

public final class RepositoryDtos {
    private RepositoryDtos() {}

    public record CreateRepositoryRequest(
            @NotBlank String provider, @URL @NotBlank String url, @NotBlank String defaultBranch) {}

    public record RepositoryResponse(UUID id, UUID projectId, String provider, String url, String defaultBranch) {}
}
