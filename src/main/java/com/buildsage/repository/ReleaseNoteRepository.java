package com.buildsage.repository;

import com.buildsage.domain.ReleaseNote;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReleaseNoteRepository extends JpaRepository<ReleaseNote, UUID> {}
