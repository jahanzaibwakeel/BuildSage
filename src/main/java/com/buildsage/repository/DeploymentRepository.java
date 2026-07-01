package com.buildsage.repository;

import com.buildsage.domain.Deployment;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeploymentRepository extends JpaRepository<Deployment, UUID> {
    List<Deployment> findTop10ByProjectIdOrderByCreatedAtDesc(UUID projectId);
}
