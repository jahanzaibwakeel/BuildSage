package com.buildsage.repository;

import com.buildsage.domain.PipelineRun;
import com.buildsage.domain.enums.PipelineStatus;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PipelineRunRepository extends JpaRepository<PipelineRun, UUID> {
    Page<PipelineRun> findByProjectId(UUID projectId, Pageable pageable);

    long countByProjectIdAndStatus(UUID projectId, PipelineStatus status);

    long countByProjectIdAndCreatedAtAfter(UUID projectId, Instant since);
}
