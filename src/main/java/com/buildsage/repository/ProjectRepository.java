package com.buildsage.repository;

import com.buildsage.domain.Project;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
    Page<Project> findByNameContainingIgnoreCase(String query, Pageable pageable);

    Page<Project> findByTeamId(UUID teamId, Pageable pageable);

    Page<Project> findByTeamIdAndNameContainingIgnoreCase(UUID teamId, String query, Pageable pageable);
}
