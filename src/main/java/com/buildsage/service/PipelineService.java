package com.buildsage.service;

import com.buildsage.api.PageResponse;
import com.buildsage.domain.AiAnalysis;
import com.buildsage.domain.PipelineJob;
import com.buildsage.domain.PipelineLog;
import com.buildsage.domain.PipelineRun;
import com.buildsage.dto.PipelineDtos.AnalysisResponse;
import com.buildsage.dto.PipelineDtos.CreatePipelineRunRequest;
import com.buildsage.dto.PipelineDtos.LogLineResponse;
import com.buildsage.dto.PipelineDtos.PipelineRunResponse;
import com.buildsage.dto.PipelineDtos.QueueStatusResponse;
import com.buildsage.exception.NotFoundException;
import com.buildsage.repository.AiAnalysisRepository;
import com.buildsage.repository.BackgroundJobRepository;
import com.buildsage.repository.PipelineJobRepository;
import com.buildsage.repository.PipelineLogRepository;
import com.buildsage.repository.PipelineRunRepository;
import com.buildsage.security.CurrentUser;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class PipelineService {
    private final ProjectService projectService;
    private final PipelineRunRepository pipelineRunRepository;
    private final PipelineJobRepository pipelineJobRepository;
    private final PipelineLogRepository pipelineLogRepository;
    private final AiAnalysisRepository aiAnalysisRepository;
    private final BackgroundJobRepository backgroundJobRepository;
    private final AnalysisQueueService analysisQueueService;
    private final AuthorizationService authorizationService;
    private final AuditService auditService;

    public PipelineService(
            ProjectService projectService,
            PipelineRunRepository pipelineRunRepository,
            PipelineJobRepository pipelineJobRepository,
            PipelineLogRepository pipelineLogRepository,
            AiAnalysisRepository aiAnalysisRepository,
            BackgroundJobRepository backgroundJobRepository,
            AnalysisQueueService analysisQueueService,
            AuthorizationService authorizationService,
            AuditService auditService) {
        this.projectService = projectService;
        this.pipelineRunRepository = pipelineRunRepository;
        this.pipelineJobRepository = pipelineJobRepository;
        this.pipelineLogRepository = pipelineLogRepository;
        this.aiAnalysisRepository = aiAnalysisRepository;
        this.backgroundJobRepository = backgroundJobRepository;
        this.analysisQueueService = analysisQueueService;
        this.authorizationService = authorizationService;
        this.auditService = auditService;
    }

    @Transactional
    public PipelineRunResponse ingest(
            CurrentUser user, UUID projectId, String idempotencyKey, CreatePipelineRunRequest request) {
        authorizationService.requireWrite(user);
        authorizationService.requireProjectAccess(user, projectId);
        return ingestInternal(user.id(), projectId, idempotencyKey, request, "PIPELINE_RUN_INGESTED");
    }

    @Transactional
    public PipelineRunResponse ingestWebhook(UUID projectId, String idempotencyKey, CreatePipelineRunRequest request) {
        return ingestInternal(null, projectId, idempotencyKey, request, "PIPELINE_RUN_WEBHOOK_INGESTED");
    }

    private PipelineRunResponse ingestInternal(
            UUID actorId, UUID projectId, String idempotencyKey, CreatePipelineRunRequest request, String auditAction) {
        String effectiveIdempotencyKey = firstPresent(idempotencyKey, request.idempotencyKey());
        if (effectiveIdempotencyKey != null) {
            var existing = pipelineRunRepository.findByProjectIdAndIdempotencyKey(projectId, effectiveIdempotencyKey);
            if (existing.isPresent()) {
                return toResponse(existing.get());
            }
        }
        var project = projectService.get(projectId);
        PipelineRun run = pipelineRunRepository.save(new PipelineRun(
                project,
                request.externalId(),
                effectiveIdempotencyKey,
                request.branch(),
                request.commitSha(),
                request.status(),
                request.startedAt(),
                request.finishedAt()));
        if (request.jobs() != null) {
            request.jobs()
                    .forEach(job ->
                            pipelineJobRepository.save(new PipelineJob(run, job.name(), job.stage(), job.status())));
        }
        for (int i = 0; i < request.logs().size(); i++) {
            pipelineLogRepository.save(
                    new PipelineLog(run, i + 1, request.logs().get(i)));
        }
        auditService.record(actorId, auditAction, "pipeline_run", run.getId().toString(), run.getExternalId());
        return toResponse(run);
    }

    @Transactional(readOnly = true)
    public PageResponse<PipelineRunResponse> list(CurrentUser user, UUID projectId, Pageable pageable) {
        authorizationService.requireProjectAccess(user, projectId);
        return PageResponse.from(
                pipelineRunRepository.findByProjectId(projectId, pageable).map(this::toResponse));
    }

    @Transactional(readOnly = true)
    public PipelineRunResponse get(CurrentUser user, UUID id) {
        PipelineRun run = find(id);
        authorizationService.requireProjectAccess(user, run.getProject().getId());
        return toResponse(run);
    }

    @Transactional(readOnly = true)
    public PageResponse<LogLineResponse> logs(CurrentUser user, UUID id, Pageable pageable) {
        PipelineRun run = find(id);
        authorizationService.requireProjectAccess(user, run.getProject().getId());
        return PageResponse.from(pipelineLogRepository
                .findByPipelineRunIdOrderByLineNumberAsc(id, pageable)
                .map(log -> new LogLineResponse(log.getLineNumber(), log.getContent())));
    }

    @Transactional(readOnly = true)
    public PageResponse<LogLineResponse> searchLogs(
            CurrentUser user, UUID id, String query, Integer fromLine, Integer toLine, Pageable pageable) {
        PipelineRun run = find(id);
        authorizationService.requireProjectAccess(user, run.getProject().getId());
        return PageResponse.from(pipelineLogRepository
                .search(id, blankToNull(query), fromLine, toLine, pageable)
                .map(log -> new LogLineResponse(log.getLineNumber(), log.getContent())));
    }

    @Transactional
    public AnalysisResponse queueAnalysis(CurrentUser user, UUID id) {
        authorizationService.requireWrite(user);
        PipelineRun run = find(id);
        authorizationService.requireProjectAccess(user, run.getProject().getId());
        AiAnalysis analysis = aiAnalysisRepository.save(new AiAnalysis(run));
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                analysisQueueService.queueLogAnalysis(analysis.getId(), run.getId());
            }
        });
        return toAnalysis(analysis);
    }

    @Transactional(readOnly = true)
    public AnalysisResponse analysis(CurrentUser user, UUID runId) {
        PipelineRun run = find(runId);
        authorizationService.requireProjectAccess(user, run.getProject().getId());
        return aiAnalysisRepository
                .findFirstByPipelineRunIdOrderByCreatedAtDesc(runId)
                .map(this::toAnalysis)
                .orElseThrow(() -> new NotFoundException("Analysis not found"));
    }

    @Transactional(readOnly = true)
    public QueueStatusResponse queueStatus(CurrentUser user, UUID runId) {
        PipelineRun run = find(runId);
        authorizationService.requireProjectAccess(user, run.getProject().getId());
        AiAnalysis analysis = aiAnalysisRepository
                .findFirstByPipelineRunIdOrderByCreatedAtDesc(runId)
                .orElseThrow(() -> new NotFoundException("Analysis not found"));
        var job = backgroundJobRepository.findFirstByTargetIdOrderByCreatedAtDesc(analysis.getId());
        return new QueueStatusResponse(
                analysis.getId(),
                analysis.getStatus(),
                job.map(backgroundJob -> backgroundJob.getType()).orElse(null),
                job.map(backgroundJob -> backgroundJob.getStatus()).orElse(null),
                job.map(backgroundJob -> backgroundJob.getMessage()).orElse(null),
                analysisQueueService.analysisQueueDepth());
    }

    private PipelineRun find(UUID id) {
        return pipelineRunRepository.findById(id).orElseThrow(() -> new NotFoundException("Pipeline run not found"));
    }

    private PipelineRunResponse toResponse(PipelineRun run) {
        return new PipelineRunResponse(
                run.getId(),
                run.getProject().getId(),
                run.getExternalId(),
                run.getIdempotencyKey(),
                run.getBranch(),
                run.getCommitSha(),
                run.getStatus(),
                run.getCreatedAt());
    }

    private AnalysisResponse toAnalysis(AiAnalysis analysis) {
        return new AnalysisResponse(
                analysis.getId(),
                analysis.getStatus(),
                analysis.getFailureType(),
                analysis.getFailedStage(),
                analysis.getRootCauseSummary(),
                analysis.getConfidenceScore(),
                analysis.getEvidenceLines(),
                analysis.getReviewState());
    }

    private String firstPresent(String first, String second) {
        String normalizedFirst = blankToNull(first);
        return normalizedFirst != null ? normalizedFirst : blankToNull(second);
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
