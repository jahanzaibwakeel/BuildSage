package com.buildsage.repository;

import com.buildsage.domain.TeamMember;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamMemberRepository extends JpaRepository<TeamMember, UUID> {
    boolean existsByTeamIdAndUserId(UUID teamId, UUID userId);

    List<TeamMember> findByUserId(UUID userId);

    List<TeamMember> findByTeamId(UUID teamId);
}
