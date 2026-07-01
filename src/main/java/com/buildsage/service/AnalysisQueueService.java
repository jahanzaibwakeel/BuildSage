package com.buildsage.service;

import com.buildsage.domain.BackgroundJob;
import com.buildsage.repository.BackgroundJobRepository;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class AnalysisQueueService {
    private static final Logger log = LoggerFactory.getLogger(AnalysisQueueService.class);
    private final BackgroundJobRepository backgroundJobRepository;
    private final StringRedisTemplate redisTemplate;
    private final AsyncAnalysisWorker asyncAnalysisWorker;

    public AnalysisQueueService(
            BackgroundJobRepository backgroundJobRepository,
            StringRedisTemplate redisTemplate,
            AsyncAnalysisWorker asyncAnalysisWorker) {
        this.backgroundJobRepository = backgroundJobRepository;
        this.redisTemplate = redisTemplate;
        this.asyncAnalysisWorker = asyncAnalysisWorker;
    }

    public void queueLogAnalysis(UUID analysisId, UUID runId) {
        backgroundJobRepository.save(new BackgroundJob("LOG_ANALYSIS", analysisId));
        try {
            redisTemplate.opsForList().leftPush("buildsage:analysis:queue", analysisId.toString());
        } catch (RuntimeException ex) {
            log.warn("Redis queue unavailable; continuing with in-process async execution");
        }
        asyncAnalysisWorker.processLogAnalysis(analysisId, runId);
    }
}
