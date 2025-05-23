package com.example.tiki.utils;

import com.example.tiki.global.exception.ForbiddenException;
import com.example.tiki.global.exception.NotFoundException;
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

import java.util.List;

@Component
@RequiredArgsConstructor
public class CheckUtil {

    private final TeamRepository teamRepository;
    private final TeamUserRepository teamUserRepository;
    private final RecruitmentRepository recruitmentRepository;

    // 팀이 존재하는지 확인
    public Team validateAndGetTeam(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException("해당 팀은 존재하지 않습니다."));
        if(team.getTeamStatus() == TeamStatus.DISBANDED) throw new NotFoundException("해당 팀은 존재하지 않습니다.");
        return team;
    }
    // 모집글이 존재하는지 확인
    public Recruitment validateAndGetRecruitment(Long recruitmentId){
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 게시글입니다."));
        if(recruitment.getRecruitmentStatus() == RecruitmentStatus.DELETED) throw new NotFoundException("존재하지 않는 게시글입니다.");
        return recruitment;
    }

    // 수행하려는 주체의 권한이 리더인지 확인
    public void validateLeaderAuthority(Long leaderId, Long teamId) {
        if(leaderId != teamUserRepository.findByTeamIdAndTeamUserRole(teamId, TeamUserRole.ROLE_LEADER).getUserId()){
            throw new ForbiddenException("해당 작업을 수행할 권한이 없습니다.");
        }
    }

    // 특정 상태의 유저 확인
    public TeamUser getTeamUserWithStatus(Long userId, Long teamId, TeamUserStatus teamUserStatus) {
        TeamUser teamUser = teamUserRepository.findByUserIdAndTeamIdAndTeamUserStatus(userId, teamId, teamUserStatus)
                .orElseThrow(() -> new ForbiddenException("해당 상태의 유저를 찾을 수 없습니다."));
        return teamUser;
    }
}
