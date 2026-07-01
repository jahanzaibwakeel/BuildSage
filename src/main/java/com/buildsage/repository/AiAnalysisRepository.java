package com.buildsage.repository;

import com.buildsage.domain.AiAnalysis;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiAnalysisRepository extends JpaRepository<AiAnalysis, UUID> {
    Optional<AiAnalysis> findFirstByPipelineRunIdOrderByCreatedAtDesc(UUID pipelineRunId);
}
