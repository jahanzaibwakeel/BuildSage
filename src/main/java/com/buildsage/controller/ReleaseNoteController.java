package com.buildsage.controller;

import com.buildsage.api.ApiResponse;
import com.buildsage.dto.ReleaseNoteDtos.GenerateReleaseNotesRequest;
import com.buildsage.dto.ReleaseNoteDtos.ReleaseNoteResponse;
import com.buildsage.security.CurrentUser;
import com.buildsage.service.ReleaseNoteService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects/{projectId}/release-notes")
public class ReleaseNoteController {
    private final ReleaseNoteService releaseNoteService;

    public ReleaseNoteController(ReleaseNoteService releaseNoteService) {
        this.releaseNoteService = releaseNoteService;
    }

    @PostMapping
    ApiResponse<ReleaseNoteResponse> generate(
            @AuthenticationPrincipal CurrentUser user,
            @PathVariable UUID projectId,
            @Valid @RequestBody GenerateReleaseNotesRequest request) {
        return ApiResponse.ok(releaseNoteService.generate(user, projectId, request));
    }
}
