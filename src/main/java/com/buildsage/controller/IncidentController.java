package com.buildsage.controller;

import com.buildsage.api.ApiResponse;
import com.buildsage.dto.IncidentDtos.CreateIncidentRequest;
import com.buildsage.dto.IncidentDtos.IncidentResponse;
import com.buildsage.security.CurrentUser;
import com.buildsage.service.IncidentService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {
    private final IncidentService incidentService;

    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @PostMapping
    ApiResponse<IncidentResponse> create(
            @AuthenticationPrincipal CurrentUser user, @Valid @RequestBody CreateIncidentRequest request) {
        return ApiResponse.ok(incidentService.create(user, request));
    }

    @PostMapping("/{id}/postmortem")
    ApiResponse<IncidentResponse> postmortem(@AuthenticationPrincipal CurrentUser user, @PathVariable UUID id) {
        return ApiResponse.ok(incidentService.postmortem(user, id));
    }
}
