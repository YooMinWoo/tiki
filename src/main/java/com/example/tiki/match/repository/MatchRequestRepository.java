package com.example.tiki.match.repository;

import com.example.tiki.match.domain.entity.MatchPost;
import com.example.tiki.match.domain.entity.MatchRequest;
import com.example.tiki.match.domain.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRequestRepository extends JpaRepository<MatchRequest, Long> {

    Optional<MatchRequest> findByMatchPostIdAndRequestStatus(Long matchPostId, RequestStatus requestStatus);
    List<MatchRequest> findByApplicantTeamId(Long teamId);

    Optional<MatchRequest> findByMatchPostIdAndApplicantTeamId(Long postId, Long teamId);

    List<MatchRequest> findByMatchPostId(Long id);

    List<MatchRequest> findAllByMatchPostIdAndRequestStatus(Long id, RequestStatus requestStatus);
}
