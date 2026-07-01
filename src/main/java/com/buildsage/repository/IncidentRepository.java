package com.buildsage.repository;

import com.buildsage.domain.Incident;
import com.buildsage.domain.enums.IncidentStatus;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncidentRepository extends JpaRepository<Incident, UUID> {
    long countByProjectIdAndStatus(UUID projectId, IncidentStatus status);
}
