package com.buildsage.service;

import com.buildsage.ai.AiProvider;
import com.buildsage.exception.NotFoundException;
import com.buildsage.repository.AiAnalysisRepository;
import com.buildsage.repository.PipelineLogRepository;
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
    private final PipelineLogRepository pipelineLogRepository;
    private final AiProvider aiProvider;

    public AsyncAnalysisWorker(
            AiAnalysisRepository aiAnalysisRepository,
            PipelineLogRepository pipelineLogRepository,
            AiProvider aiProvider) {
        this.aiAnalysisRepository = aiAnalysisRepository;
        this.pipelineLogRepository = pipelineLogRepository;
        this.aiProvider = aiProvider;
    }

    @Async
    @Transactional
    public void processLogAnalysis(UUID analysisId, UUID runId) {
        var analysis = aiAnalysisRepository
                .findById(analysisId)
                .orElseThrow(() -> new NotFoundException("Analysis not found"));
        analysis.markProcessing();
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
            log.info("Completed AI analysis {}", analysisId);
        } catch (RuntimeException ex) {
            analysis.fail(ex.getMessage());
            log.error("Failed AI analysis {}", analysisId, ex);
        }
    }
}
