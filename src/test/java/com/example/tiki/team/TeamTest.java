//package com.example.tiki.team;
//
//import com.example.tiki.auth.domain.Role;
//import com.example.tiki.auth.domain.User;
//import com.example.tiki.auth.repository.AuthRepository;
//import com.example.tiki.init.UserInit;
//import com.example.tiki.notifircation.domain.Notification;
//import com.example.tiki.notifircation.dto.NotificationDto;
//import com.example.tiki.notifircation.service.NotificationService;
//import com.example.tiki.team.domain.*;
//import com.example.tiki.team.dto.TeamCreateRequestDto;
//import com.example.tiki.team.repository.TeamRepository;
//import com.example.tiki.team.repository.TeamUserHistoryRepository;
//import com.example.tiki.team.repository.TeamUserRepository;
//import com.example.tiki.team.service.TeamService;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@Transactional
//class TeamTest {
//
//    @Autowired
//    TeamService teamService;
//
//    @Autowired
//    AuthRepository authRepository;
//
//    @Autowired
//    TeamRepository teamRepository;
//
//    @Autowired
//    TeamUserRepository teamUserRepository;
//
//    @Autowired
//    TeamUserHistoryRepository teamUserHistoryRepository;
//
//    @Autowired
//    NotificationService notificationService;
//
//    private User leader;
//    private User member;
//    private Team team;
//    private TeamUser teamUser;
//
//    @BeforeEach
//    void beforeEach(){
//        // 유저, 팀
//        leader = authRepository.save(
//                User.builder()
//                        .name("leader")
//                        .email("leader@naver.com")
//                        .role(Role.ROLE_USER)
//                        .build());
//
//        member = authRepository.save(
//                User.builder()
//                        .name("member")
//                        .email("member@naver.com")
//                        .role(Role.ROLE_USER)
//                        .build());
//
//        team = teamRepository.save(Team.builder()
//                .teamName("푸에르 FC")
//                .teamDescription("푸에르 FC입니다.")
//                .teamStatus(TeamStatus.OPEN)
//                .build());
//
//        teamUser = teamUserRepository.save(TeamUser.builder()
//                    .userId(leader.getId())
//                    .teamId(team.getId())
//                    .teamUserRole(TeamUserRole.ROLE_LEADER)
//                    .teamUserStatus(TeamUserStatus.APPROVED)
//                    .build());
//    }
//
//    @Test
//    void 리더확인(){
//        Long leaderId = teamUserRepository.findLeaderId(team.getId());
//
//        assertEquals(leaderId, leader.getId());
//    }
//
//    @Test
//    public void 가입신청(){
//        // when
//        TeamUser teamUser = teamService.teamJoinRequest(member, team.getId());
//
//        // waiting인지 확인, notification 확인
//        List<NotificationDto> notificationDtos = notificationService.getNotification(leader.getId());
//        for(NotificationDto dto : notificationDtos){
//            assertEquals(dto.getTargetId(), member.getId());
//            assertEquals(dto.getMessage(), member.getName()+"님께서 가입 요청을 보냈습니다.");
//        }
//
//        assertEquals(member.getId(), teamUser.getUserId());
//        assertEquals(team.getId(), teamUser.getTeamId());
//        assertEquals(TeamRole.ROLE_WAITING, teamUser.getTeamRole());  // 가입 요청 상태는 ROLE_WATTING이어야 한다
//        assertNotNull(teamUser.getCreatedDate());  // 생성일자가 null이 아니어야 한다
//    }
//
//    @Test
//    void 가입수락(){
//        // 가입신청
//        teamService.teamJoinRequest(member, team.getId());
//
//        // 가입수락
//        teamService.approveTeamJoinRequest(leader.getId(), member.getId(), team.getId()); //Long leaderId, Long userId, Long teamId
//
//        // 결과
//        for (NotificationDto dto : notificationService.getNotification(member.getId())) {
//            assertEquals(dto.getMessage(), "푸에르 FC팀에서 가입을 수락했습니다.");
//            assertEquals(dto.getTargetId(), team.getId());
//        }
//
//    }
//
//    @Test
//    void 가입거절(){
//        // 가입신청
//        teamService.teamJoinRequest(member, team.getId());
//
//        // 가입수락
//        teamService.rejectTeamJoinRequest(leader.getId(), member.getId(), team.getId()); //Long leaderId, Long userId, Long teamId
//
//        // 결과
//        for (NotificationDto dto : notificationService.getNotification(member.getId())) {
//            assertEquals(dto.getMessage(), "푸에르 FC팀에서 가입을 거절했습니다.");
//            assertEquals(dto.getTargetId(), team.getId());
//        }
//
//    }
//
//    @Test
//    void 팀_탈퇴(){
//        // 가입신청
//        teamService.teamJoinRequest(member, team.getId());
//
//        // 가입수락
//        teamService.rejectTeamJoinRequest(leader.getId(), member.getId(), team.getId()); //Long leaderId, Long userId, Long teamId
//
//        // 팀 탈퇴
//        teamService.leaveTeam(member, team.getId());
//
////        for(teamUserRepository.)
//
//        // 결과
//        for (NotificationDto dto : notificationService.getNotification(member.getId())) {
//            assertEquals(dto.getMessage(), "푸에르 FC팀에서 가입을 거절했습니다.");
//            assertEquals(dto.getTargetId(), team.getId());
//        }
//
//    }
//}