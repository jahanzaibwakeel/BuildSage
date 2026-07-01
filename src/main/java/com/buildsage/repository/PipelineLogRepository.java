package com.buildsage.repository;

import com.buildsage.domain.PipelineLog;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PipelineLogRepository extends JpaRepository<PipelineLog, UUID> {
    Page<PipelineLog> findByPipelineRunIdOrderByLineNumberAsc(UUID pipelineRunId, Pageable pageable);

    List<PipelineLog> findTop200ByPipelineRunIdOrderByLineNumberAsc(UUID pipelineRunId);

    @Query(
            """
            select log from PipelineLog log
            where log.pipelineRun.id = :pipelineRunId
              and (:query is null or lower(log.content) like lower(concat('%', :query, '%')))
              and (:fromLine is null or log.lineNumber >= :fromLine)
              and (:toLine is null or log.lineNumber <= :toLine)
            order by log.lineNumber asc
            """)
    Page<PipelineLog> search(
            @Param("pipelineRunId") UUID pipelineRunId,
            @Param("query") String query,
            @Param("fromLine") Integer fromLine,
            @Param("toLine") Integer toLine,
            Pageable pageable);
}
