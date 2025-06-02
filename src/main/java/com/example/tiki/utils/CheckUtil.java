package com.example.tiki.utils;

import com.example.tiki.global.exception.ForbiddenException;
import com.example.tiki.global.exception.NotFoundException;
import com.example.tiki.match.domain.entity.MatchPost;
import com.example.tiki.match.domain.entity.MatchRequest;
import com.example.tiki.match.domain.enums.MatchStatus;
import com.example.tiki.match.domain.enums.RequestStatus;
import com.example.tiki.match.repository.MatchPostRepository;
import com.example.tiki.match.repository.MatchRequestRepository;
import com.example.tiki.notice.domain.entity.Notice;
import com.example.tiki.notice.domain.enums.NoticeStatus;
import com.example.tiki.notice.repository.NoticeRepository;
import com.example.tiki.recruitment.domain.entity.Recruitment;
import com.example.tiki.recruitment.domain.enums.RecruitmentStatus;
import com.example.tiki.recruitment.repository.RecruitmentRepository;
import com.example.tiki.team.domain.entity.Team;
import com.example.tiki.team.domain.entity.TeamUser;
import com.example.tiki.team.domain.enums.TeamStatus;
import com.example.tiki.team.domain.enums.TeamUserRole;
import com.example.tiki.team.domain.enums.TeamUserStatus;
import com.example.tiki.team.repository.TeamRepository;
import com.example.tiki.team.repository.TeamUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CheckUtil {

    private final TeamRepository teamRepository;
    private final TeamUserRepository teamUserRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final MatchPostRepository matchPostRepository;
    private final MatchRequestRepository matchRequestRepository;
    private final NoticeRepository noticeRepository;

    // 팀이 존재하는지 확인
    public Team validateAndGetTeam(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException("해당 팀은 존재하지 않습니다."));
        if(team.getTeamStatus() == TeamStatus.DISBANDED) throw new NotFoundException("해당 팀은 존재하지 않습니다.");
        return team;
    }

    // 공지사항이 존재하는지 확인
    public Notice validateAndGetNotion(Long notionId) {
        Notice notice = noticeRepository.findById(notionId)
                .orElseThrow(() -> new NotFoundException("해당 공지사항은 존재하지 않습니다."));
        if(notice.getNoticeStatus() == NoticeStatus.DELETED) throw new NotFoundException("해당 공지사항은 존재하지 않습니다.");
        return notice;
    }

    // 모집글이 존재하는지 확인
    public Recruitment validateAndGetRecruitment(Long recruitmentId){
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 게시글입니다."));
        if(recruitment.getRecruitmentStatus() == RecruitmentStatus.DELETED) throw new NotFoundException("존재하지 않는 게시글입니다.");
        return recruitment;
    }

    // 매칭글이 존재하는지 확인
    public MatchPost validateAndGetMatchPost(Long matchPostId){
        MatchPost matchPost = matchPostRepository.findById(matchPostId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 게시글입니다."));
        if(matchPost.getMatchStatus() == MatchStatus.DELETED
                || matchPost.getMatchStatus() == MatchStatus.DELETED_BY_ADMIN) throw new NotFoundException("존재하지 않는 게시글입니다.");
        return matchPost;
    }

    // 수행하려는 주체의 권한이 리더인지 확인
    public TeamUser validateLeaderAuthority(Long leaderId, Long teamId) {
        TeamUser teamUser = teamUserRepository.findByTeamIdAndTeamUserRole(teamId, TeamUserRole.ROLE_LEADER);
        if(teamUser == null || teamUser.getUserId() != leaderId) throw new ForbiddenException("해당 작업을 수행할 권한이 없습니다.");

        return teamUser;
//        if(leaderId != teamUserRepository.findByTeamIdAndTeamUserRole(teamId, TeamUserRole.ROLE_LEADER).getUserId()){
//            throw new ForbiddenException("해당 작업을 수행할 권한이 없습니다.");
//        }
    }

    // 특정 상태의 유저 확인
    public TeamUser getTeamUserWithStatus(Long userId, Long teamId, TeamUserStatus teamUserStatus) {
        TeamUser teamUser = teamUserRepository.findByUserIdAndTeamIdAndTeamUserStatus(userId, teamId, teamUserStatus)
                .orElseThrow(() -> new ForbiddenException("해당 상태의 유저를 찾을 수 없습니다."));
        return teamUser;
    }

    // 특정 팀의

    public MatchRequest getPendingMatchRequest(Long matchRequestId) {
        MatchRequest matchRequest = matchRequestRepository.findById(matchRequestId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 매칭 신청입니다."));
        if(matchRequest.getRequestStatus() != RequestStatus.PENDING) throw new IllegalArgumentException("현재 대기중인 상태의 매칭 신청이 아닙니다.");
        return matchRequest;
    }

    public MatchRequest getAcceptedMatchRequest(Long matchRequestId) {
        MatchRequest matchRequest = matchRequestRepository.findById(matchRequestId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 매칭 신청입니다."));
        if(matchRequest.getRequestStatus() != RequestStatus.ACCEPTED) throw new IllegalArgumentException("현재 대기중인 상태의 매칭 신청이 아닙니다.");
        return matchRequest;
    }
}
