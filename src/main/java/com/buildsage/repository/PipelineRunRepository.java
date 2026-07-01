package com.buildsage.repository;

import com.buildsage.domain.PipelineRun;
import com.buildsage.domain.enums.PipelineStatus;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PipelineRunRepository extends JpaRepository<PipelineRun, UUID> {
    Page<PipelineRun> findByProjectId(UUID projectId, Pageable pageable);

    Optional<PipelineRun> findByProjectIdAndIdempotencyKey(UUID projectId, String idempotencyKey);

    long countByProjectId(UUID projectId);

    long countByProjectIdAndStatus(UUID projectId, PipelineStatus status);

    long countByProjectIdAndCreatedAtAfter(UUID projectId, Instant since);

    long countByProjectIdAndStatusAndCreatedAtAfter(UUID projectId, PipelineStatus status, Instant since);
}
