package com.buildsage.repository;

import com.buildsage.domain.IncidentEvent;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncidentEventRepository extends JpaRepository<IncidentEvent, UUID> {}
