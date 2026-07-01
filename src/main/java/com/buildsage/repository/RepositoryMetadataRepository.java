package com.buildsage.repository;

import com.buildsage.domain.RepositoryMetadata;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositoryMetadataRepository extends JpaRepository<RepositoryMetadata, UUID> {
    Page<RepositoryMetadata> findByProjectId(UUID projectId, Pageable pageable);
}
