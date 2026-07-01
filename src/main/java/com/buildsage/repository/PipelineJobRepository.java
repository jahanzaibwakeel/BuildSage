package com.buildsage.repository;

import com.buildsage.domain.PipelineJob;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PipelineJobRepository extends JpaRepository<PipelineJob, UUID> {}
