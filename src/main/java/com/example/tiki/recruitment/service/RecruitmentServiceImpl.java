package com.example.tiki.recruitment.service;

import com.example.tiki.auth.domain.Role;
import com.example.tiki.auth.domain.User;
import com.example.tiki.follow.domain.Follow;
import com.example.tiki.follow.repository.FollowRepository;
import com.example.tiki.notifircation.domain.Notification;
import com.example.tiki.notifircation.domain.NotificationType;
import com.example.tiki.notifircation.repository.NotificationRepository;
import com.example.tiki.recruitment.domain.entity.Recruitment;
import com.example.tiki.recruitment.domain.enums.RecruitmentStatus;
import com.example.tiki.recruitment.dto.*;
import com.example.tiki.recruitment.repository.RecruitmentRepository;
import com.example.tiki.team.domain.entity.Team;
import com.example.tiki.team.domain.entity.TeamUser;
import com.example.tiki.team.domain.enums.TeamStatus;
import com.example.tiki.team.domain.enums.TeamUserRole;
import com.example.tiki.team.domain.enums.TeamUserStatus;
import com.example.tiki.team.repository.TeamRepository;
import com.example.tiki.team.repository.TeamUserRepository;
import com.example.tiki.utils.CheckUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecruitmentServiceImpl implements RecruitmentService{

    private final CheckUtil checkUtil;
    private final RecruitmentRepository recruitmentRepository;
    private final FollowRepository followRepository;
    private final NotificationRepository notificationRepository;
    private final TeamRepository teamRepository;
    private final TeamUserRepository teamUserRepository;


    // 모집글 등록
    @Transactional
    public void createRecruitmentPost(Long userId, Long teamId, RecruitmentCreateRequest recruitmentCreateRequest){
        // 팀 존재 확인
        Team team = checkUtil.validateAndGetTeam(teamId);
        if(team.getTeamStatus() == TeamStatus.INACTIVE) throw new IllegalStateException("비활성화 상태입니다.");

        // 리더 권한 확인
        checkUtil.validateLeaderAuthority(userId, teamId);

        // 글 생성
        Recruitment recruitment = Recruitment.create(teamId, recruitmentCreateRequest);

        // db insert
        recruitmentRepository.save(recruitment);

        // 팔로워에게 알림 전송
        List<Follow> follows = followRepository.findByTeamId(teamId);
        for (Follow follow : follows) {
            notificationRepository.save(Notification.builder()
                        .userId(follow.getUserId())
                        .message(team.getTeamName() + "팀이 팀원 모집글을 올렸습니다.")
                        .notificationType(NotificationType.RECRUIT)
                        .targetId(recruitment.getId())
                        .build());
        }

    };

    // 모집글 수정
    @Transactional
    public void updateRecruitmentPost(Long userId, RecruitmentUpdateRequest request){
        // 팀 존재 확인
        Team team = checkUtil.validateAndGetTeam(request.getRecruitmentId());
        if(team.getTeamStatus() == TeamStatus.INACTIVE) throw new IllegalStateException("비활성화 상태인 팀입니다.");

        // 존재하는 모집 글인지 확인
        Recruitment recruitment = checkUtil.validateAndGetRecruitment(request.getRecruitmentId());

        if(recruitment.getRecruitmentStatus() == RecruitmentStatus.CLOSE) throw new IllegalStateException("모집 마감된 게시글입니다.");

        // 리더 권한 확인
        checkUtil.validateLeaderAuthority(userId, recruitment.getTeamId());

        recruitment.update(request);
    }

    // 모집글 마감
    @Transactional
    public void closeRecruitmentPost(Long userId, Long recruitmentId){
        // 존재하는 모집 글인지 확인
        Recruitment recruitment = checkUtil.validateAndGetRecruitment(recruitmentId);

        // 리더 권한 확인
        checkUtil.validateLeaderAuthority(userId, recruitment.getTeamId());

        if(recruitment.getRecruitmentStatus() == RecruitmentStatus.CLOSE) throw new IllegalStateException("이미 닫힌 모집 공고입니다.");
        if(recruitment.getRecruitmentStatus() == RecruitmentStatus.DELETED) throw new IllegalStateException("삭제된 공고는 처리할 수 없습니다.");

        recruitment.closed();
    }

    // 모집글 삭제
    public void deleteRecruitmentPost(User user, Long recruitmentId){
        // 존재하는 모집 글인지 확인
        Recruitment recruitment = checkUtil.validateAndGetRecruitment(recruitmentId);

        // 존재하는 팀인지 확인
        Team team = checkUtil.validateAndGetTeam(recruitment.getTeamId());

        // 관리자인지 확인
        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;

        // 리더 권한 확인
        if(!isAdmin) checkUtil.validateLeaderAuthority(user.getId(), recruitment.getTeamId());

        List<TeamUser> teamUsers = teamUserRepository.findAllByTeamIdAndTeamUserStatus(team.getId(), TeamUserStatus.WAITING);
        for (TeamUser teamUser : teamUsers) {
            // 신청 대기 -> 거절
            teamUser.changeStatus(TeamUserStatus.REJECTED);

            // 거절 되었다고 알림 발송
            notificationRepository.save(
                    Notification.builder()
                            .userId(teamUser.getUserId())
                            .message("글이 삭제 되어 신청이 거절 되었습니다.")
                            .notificationType(NotificationType.RECRUITLIST)
                            .targetId(null)
                            .build()
            );
        }

        // 관리자의 경우 삭제 및 알림 발송
        if(isAdmin){
            TeamUser leader = teamUserRepository.findByTeamIdAndTeamUserRole(team.getId(), TeamUserRole.ROLE_LEADER);
            notificationRepository.save(
                    Notification.builder()
                            .userId(leader.getUserId())
                            .message("관리자에 의해 모집 글이 삭제되었습니다.")
                            .notificationType(NotificationType.RECRUITLIST)
                            .targetId(null)
                            .build()
            );
            recruitment.deletedByAdmin();
        } else {
            recruitment.deleted();
        }
    }

    // 모집글 리오픈
    public void reopenRecruitmentPost(Long userId, Long recruitmentId){
        // 존재하는 모집 글인지 확인
        Recruitment recruitment = checkUtil.validateAndGetRecruitment(recruitmentId);

        // 리더 권한 확인
        checkUtil.validateLeaderAuthority(userId, recruitment.getTeamId());

        if(recruitment.getRecruitmentStatus() == RecruitmentStatus.OPEN) throw new IllegalStateException("이미 모집 중인 공고입니다.");

        recruitment.reopen();
    }

    // 모집글 조회(키워드, 상태 필터)
    public List<RecruitmentSearchResultDto> getRecruitmentSearchResult(String keyword, RecruitmentStatusVisible status) {
        List<RecruitmentSearchResultDto> resultDtoList = new ArrayList<>();
        keyword = keyword == null ? "" : keyword;
        RecruitmentStatus recruitmentStatus = status == null ? null : status.toRecruitmentStatus();

        List<Recruitment> recruitments = (status == null)
                ? recruitmentRepository.findByTitleContainingAndRecruitmentStatusNot(keyword, RecruitmentStatus.DELETED)
                : recruitmentRepository.findByTitleContainingAndRecruitmentStatus(keyword, recruitmentStatus);

        for (Recruitment recruitment : recruitments) {
            Team team = checkUtil.validateAndGetTeam(recruitment.getTeamId());
            resultDtoList.add(RecruitmentSearchResultDto.from(recruitment,team));
        }
        return resultDtoList;

    }

    // 모집글 상세 조회
    @Override
    public RecruitmentDetailDto getRecruitmentDetail(Long recruitmentId) {
        Recruitment recruitment = checkUtil.validateAndGetRecruitment(recruitmentId);
        Team team = checkUtil.validateAndGetTeam(recruitment.getTeamId());
        return RecruitmentDetailDto.from(recruitment, team);
    }

}
