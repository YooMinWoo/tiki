package com.example.tiki.team.repository;

import com.example.tiki.team.domain.TeamUser;
import com.example.tiki.team.domain.TeamUserHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamUserHistoryRepository extends JpaRepository<TeamUserHistory, Long> {

}
