package com.buildsage.controller;

import com.buildsage.api.ApiResponse;
import com.buildsage.api.PageResponse;
import com.buildsage.dto.PipelineDtos.AnalysisResponse;
import com.buildsage.dto.PipelineDtos.CreatePipelineRunRequest;
import com.buildsage.dto.PipelineDtos.LogLineResponse;
import com.buildsage.dto.PipelineDtos.PipelineRunResponse;
import com.buildsage.security.CurrentUser;
import com.buildsage.service.PipelineService;
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
public class PipelineController {
    private final PipelineService pipelineService;

    public PipelineController(PipelineService pipelineService) {
        this.pipelineService = pipelineService;
    }

    @PostMapping("/projects/{projectId}/pipeline-runs")
    ApiResponse<PipelineRunResponse> ingest(
            @AuthenticationPrincipal CurrentUser user,
            @PathVariable UUID projectId,
            @Valid @RequestBody CreatePipelineRunRequest request) {
        return ApiResponse.ok(pipelineService.ingest(user, projectId, request));
    }

    @GetMapping("/projects/{projectId}/pipeline-runs")
    ApiResponse<PageResponse<PipelineRunResponse>> list(
            @AuthenticationPrincipal CurrentUser user, @PathVariable UUID projectId, Pageable pageable) {
        return ApiResponse.ok(pipelineService.list(user, projectId, pageable));
    }

    @GetMapping("/pipeline-runs/{id}")
    ApiResponse<PipelineRunResponse> get(@AuthenticationPrincipal CurrentUser user, @PathVariable UUID id) {
        return ApiResponse.ok(pipelineService.get(user, id));
    }

    @GetMapping("/pipeline-runs/{id}/logs")
    ApiResponse<PageResponse<LogLineResponse>> logs(
            @AuthenticationPrincipal CurrentUser user, @PathVariable UUID id, Pageable pageable) {
        return ApiResponse.ok(pipelineService.logs(user, id, pageable));
    }

    @PostMapping("/pipeline-runs/{id}/analyze")
    ApiResponse<AnalysisResponse> analyze(@AuthenticationPrincipal CurrentUser user, @PathVariable UUID id) {
        return ApiResponse.ok(pipelineService.queueAnalysis(user, id));
    }

    @GetMapping("/pipeline-runs/{id}/analysis")
    ApiResponse<AnalysisResponse> analysis(@AuthenticationPrincipal CurrentUser user, @PathVariable UUID id) {
        return ApiResponse.ok(pipelineService.analysis(user, id));
    }
}
