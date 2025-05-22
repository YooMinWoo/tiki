package com.example.tiki.recruitment.service;

import com.example.tiki.follow.domain.Follow;
import com.example.tiki.follow.repository.FollowRepository;
import com.example.tiki.global.exception.ForbiddenException;
import com.example.tiki.global.exception.NotFoundException;
import com.example.tiki.notifircation.domain.Notification;
import com.example.tiki.notifircation.domain.NotificationType;
import com.example.tiki.notifircation.repository.NotificationRepository;
import com.example.tiki.recruitment.domain.entity.Recruitment;
import com.example.tiki.recruitment.domain.enums.RecruitmentStatus;
import com.example.tiki.recruitment.dto.RecruitmentCreateRequest;
import com.example.tiki.recruitment.dto.RecruitmentUpdateRequest;
import com.example.tiki.recruitment.repository.RecruitmentRepository;
import com.example.tiki.team.domain.entity.Team;
import com.example.tiki.team.domain.enums.TeamStatus;
import com.example.tiki.team.domain.enums.TeamUserRole;
import com.example.tiki.utils.CheckUtil;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecruitmentServiceImpl implements RecruitmentService{

    private final CheckUtil checkUtil;
    private final RecruitmentRepository recruitmentRepository;
    private final FollowRepository followRepository;
    private final NotificationRepository notificationRepository;


    // 모집글 등록
    @Transactional
    public void createRecruitmentPost(Long userId, Long teamId, RecruitmentCreateRequest recruitmentCreateRequest){
        // 팀 존재 확인
        Team team = checkUtil.getOrElseThrow(teamId);
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
        Team team = checkUtil.getOrElseThrow(request.getRecruitmentId());
        if(team.getTeamStatus() == TeamStatus.INACTIVE) throw new IllegalStateException("비활성화 상태인 팀입니다.");

        Recruitment recruitment = recruitmentRepository.findById(request.getRecruitmentId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 게시글입니다."));

        if(recruitment.getRecruitmentStatus() == RecruitmentStatus.CLOSE) throw new IllegalStateException("모집 마감된 게시글입니다.");

        // 리더 권한 확인
        checkUtil.validateLeaderAuthority(userId, recruitment.getTeamId());

        recruitment.update(request);
    }

    // 모집글 마감
    @Transactional
    public void closeRecruitmentPost(Long userId, Long recruitmentId){
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 게시글입니다."));

        // 리더 권한 확인
        checkUtil.validateLeaderAuthority(userId, recruitment.getTeamId());

        if(recruitment.getRecruitmentStatus() == RecruitmentStatus.CLOSE) throw new IllegalStateException("이미 닫힌 모집 공고입니다.");
        if(recruitment.getRecruitmentStatus() == RecruitmentStatus.DELETED) throw new IllegalStateException("삭제된 공고는 처리할 수 없습니다.");

        recruitment.closed();
    }

    // 모집글 삭제
    public void deleteRecruitmentPost(){};



}
