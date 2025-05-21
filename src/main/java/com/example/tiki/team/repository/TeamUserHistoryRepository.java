package com.example.tiki.team.repository;

import com.example.tiki.team.domain.entity.TeamUserHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamUserHistoryRepository extends JpaRepository<TeamUserHistory, Long> {
    void deleteAllByTeamUserId(Long teamUserId);

    List<TeamUserHistory> findByUserId(Long userId);
}
