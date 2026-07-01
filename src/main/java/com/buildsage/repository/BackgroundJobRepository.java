package com.buildsage.repository;

import com.buildsage.domain.BackgroundJob;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BackgroundJobRepository extends JpaRepository<BackgroundJob, UUID> {}
