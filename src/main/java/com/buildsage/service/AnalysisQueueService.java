package com.buildsage.service;

import com.buildsage.domain.BackgroundJob;
import com.buildsage.repository.BackgroundJobRepository;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void queueLogAnalysis(UUID analysisId, UUID runId) {
        backgroundJobRepository.save(new BackgroundJob("LOG_ANALYSIS", analysisId));
        try {
            redisTemplate.opsForList().leftPush("buildsage:analysis:queue", analysisId.toString());
        } catch (RuntimeException ex) {
            log.warn("Redis queue unavailable; continuing with in-process async execution");
        }
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    asyncAnalysisWorker.processLogAnalysis(analysisId, runId);
                }
            });
        } else {
            asyncAnalysisWorker.processLogAnalysis(analysisId, runId);
        }
    }

    public Long analysisQueueDepth() {
        try {
            return redisTemplate.opsForList().size("buildsage:analysis:queue");
        } catch (RuntimeException ex) {
            return null;
        }
    }
}
