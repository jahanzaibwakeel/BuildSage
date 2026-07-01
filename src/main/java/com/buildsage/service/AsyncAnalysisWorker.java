package com.buildsage.service;

import com.buildsage.ai.AiProvider;
import com.buildsage.domain.enums.AnalysisStatus;
import com.buildsage.exception.NotFoundException;
import com.buildsage.repository.AiAnalysisRepository;
import com.buildsage.repository.BackgroundJobRepository;
import com.buildsage.repository.PipelineLogRepository;
import com.buildsage.repository.TeamMemberRepository;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AsyncAnalysisWorker {
    private static final Logger log = LoggerFactory.getLogger(AsyncAnalysisWorker.class);
    private final AiAnalysisRepository aiAnalysisRepository;
    private final BackgroundJobRepository backgroundJobRepository;
    private final PipelineLogRepository pipelineLogRepository;
    private final AiProvider aiProvider;
    private final TeamMemberRepository teamMemberRepository;
    private final NotificationService notificationService;

    public AsyncAnalysisWorker(
            AiAnalysisRepository aiAnalysisRepository,
            BackgroundJobRepository backgroundJobRepository,
            PipelineLogRepository pipelineLogRepository,
            AiProvider aiProvider,
            TeamMemberRepository teamMemberRepository,
            NotificationService notificationService) {
        this.aiAnalysisRepository = aiAnalysisRepository;
        this.backgroundJobRepository = backgroundJobRepository;
        this.pipelineLogRepository = pipelineLogRepository;
        this.aiProvider = aiProvider;
        this.teamMemberRepository = teamMemberRepository;
        this.notificationService = notificationService;
    }

    @Async
    @Transactional
    public void processLogAnalysis(UUID analysisId, UUID runId) {
        var analysis = aiAnalysisRepository
                .findById(analysisId)
                .orElseThrow(() -> new NotFoundException("Analysis not found"));
        analysis.markProcessing();
        var job = backgroundJobRepository.findFirstByTargetIdOrderByCreatedAtDesc(analysisId);
        job.ifPresent(backgroundJob -> backgroundJob.mark(AnalysisStatus.PROCESSING, "Log analysis started"));
        try {
            var lines = pipelineLogRepository.findTop200ByPipelineRunIdOrderByLineNumberAsc(runId).stream()
                    .map(log -> log.getLineNumber() + ": " + log.getContent())
                    .toList();
            var result = aiProvider.analyzeLogs(lines);
            analysis.complete(
                    result.failureType(),
                    result.failedStage(),
                    result.summary(),
                    result.confidence(),
                    String.join("\n", result.evidenceLines()));
            job.ifPresent(backgroundJob -> backgroundJob.mark(AnalysisStatus.COMPLETED, "Log analysis completed"));
            notifyTeamMembers(analysis);
            log.info("Completed AI analysis {}", analysisId);
        } catch (RuntimeException ex) {
            analysis.fail(ex.getMessage());
            job.ifPresent(backgroundJob -> backgroundJob.mark(AnalysisStatus.FAILED, ex.getMessage()));
            log.error("Failed AI analysis {}", analysisId, ex);
        }
    }

    private void notifyTeamMembers(com.buildsage.domain.AiAnalysis analysis) {
        var pipelineRun = analysis.getPipelineRun();
        var teamId = pipelineRun.getProject().getTeam().getId();
        String message = "Analysis completed for pipeline run %s with %s"
                .formatted(pipelineRun.getExternalId(), analysis.getFailureType());
        teamMemberRepository
                .findByTeamId(teamId)
                .forEach(member -> notificationService.create(member.getUser().getId(), "ANALYSIS", message));
    }
}
