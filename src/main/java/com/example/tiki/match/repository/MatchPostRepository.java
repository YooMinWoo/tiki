package com.example.tiki.match.repository;

import com.example.tiki.match.domain.entity.MatchPost;
import com.example.tiki.match.domain.enums.MatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MatchPostRepository extends JpaRepository<MatchPost, Long>, MatchPostRepositoryCustom {

    List<MatchPost> findByMatchStatusAndEndTimeBefore(MatchStatus matchStatus, LocalDateTime now);
}
