package com.example.tiki.team.repository;

import com.example.tiki.team.domain.entity.Team;
import com.example.tiki.team.domain.enums.TeamStatus;
import com.example.tiki.team.dto.TeamDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByTeamStatus(TeamStatus teamStatus);

    List<Team> findByTeamStatusIn(List<TeamStatus> teamStatuses);

    // 키워드를 포함한 팀 조회(해체된 팀 제외)
    List<Team> findByTeamNameContainingAndTeamStatusNot(String keyword, TeamStatus teamStatus);

    // 키워드를 포함, 특정 상태의 팀 조회
    List<Team> findByTeamNameContainingAndTeamStatus(String keyword, TeamStatus teamStatus);
}
