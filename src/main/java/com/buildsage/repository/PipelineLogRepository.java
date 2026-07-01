package com.buildsage.repository;

import com.buildsage.domain.PipelineLog;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PipelineLogRepository extends JpaRepository<PipelineLog, UUID> {
    Page<PipelineLog> findByPipelineRunIdOrderByLineNumberAsc(UUID pipelineRunId, Pageable pageable);

    List<PipelineLog> findTop200ByPipelineRunIdOrderByLineNumberAsc(UUID pipelineRunId);
}
