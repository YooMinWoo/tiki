package com.example.tiki.team.repository;

import com.example.tiki.team.domain.entity.TeamUser;
import com.example.tiki.team.domain.enums.TeamUserRole;
import com.example.tiki.team.domain.enums.TeamUserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamUserRepository extends JpaRepository<TeamUser, Long> {

    Optional<TeamUser> findByUserIdAndTeamId(Long userId, Long teamId);

    TeamUser findByTeamIdAndTeamUserRole(Long teamId, TeamUserRole teamUserRole);

    Optional<TeamUser> findByUserIdAndTeamIdAndTeamUserStatus(Long userId, Long teamId, TeamUserStatus teamUserStatus);

    List<TeamUser> findAllByTeamIdAndTeamUserStatus(Long teamId, TeamUserStatus teamUserStatus);

    // teamUserStatus 리스트에 속하는 모든 teamUser들
    List<TeamUser> findByTeamIdAndTeamUserStatusIn(Long teamId, List<TeamUserStatus> teamUserStatuses);

    // 내가 속한 팀 전제 리스트
    List<TeamUser> findByUserIdAndTeamUserStatus(Long userId, TeamUserStatus teamUserStatus);

}
