package com.buildsage.controller;

import com.buildsage.api.ApiResponse;
import com.buildsage.dto.PipelineDtos.CreatePipelineRunRequest;
import com.buildsage.dto.PipelineDtos.PipelineRunResponse;
import com.buildsage.security.WebhookSignatureService;
import com.buildsage.service.PipelineService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.UUID;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/webhooks/github")
public class WebhookController {
    private final ObjectMapper objectMapper;
    private final Validator validator;
    private final PipelineService pipelineService;
    private final WebhookSignatureService webhookSignatureService;

    public WebhookController(
            ObjectMapper objectMapper,
            Validator validator,
            PipelineService pipelineService,
            WebhookSignatureService webhookSignatureService) {
        this.objectMapper = objectMapper;
        this.validator = validator;
        this.pipelineService = pipelineService;
        this.webhookSignatureService = webhookSignatureService;
    }

    @PostMapping("/projects/{projectId}/pipeline-runs")
    ApiResponse<PipelineRunResponse> ingestPipelineRun(
            @PathVariable UUID projectId,
            @RequestHeader("X-Hub-Signature-256") String signature,
            @RequestHeader(value = "X-GitHub-Delivery", required = false) String deliveryId,
            @RequestBody String payload)
            throws HttpMessageNotReadableException {
        webhookSignatureService.verifyGithubSignature(payload, signature);
        CreatePipelineRunRequest request = readPayload(payload);
        var violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        return ApiResponse.ok(pipelineService.ingestWebhook(projectId, deliveryId, request));
    }

    private CreatePipelineRunRequest readPayload(String payload) {
        try {
            return objectMapper.readValue(payload, CreatePipelineRunRequest.class);
        } catch (JsonProcessingException ex) {
            throw new HttpMessageNotReadableException("Malformed or unreadable request body", ex, null);
        }
    }
}
