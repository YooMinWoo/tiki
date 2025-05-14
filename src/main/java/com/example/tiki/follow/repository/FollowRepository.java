package com.example.tiki.follow.repository;

import com.example.tiki.follow.domain.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByUserIdAndTeamId(Long userId, Long teamId);

    List<Follow> findByTeamId(Long teamId);

    List<Follow> findByUserId(Long userId);
}
