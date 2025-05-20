package com.example.tiki.team.domain;

public enum TeamRole {
    ROLE_MEMBER,    // 일반 회원
    ROLE_MANAGER,   // 매니저
    ROLE_LEADER,    // 감독
    ROLE_WAITING,   // 승인 대기
    ROLE_REJECTED,  // 승인 거절
    ROLE_KICKED,    // 방출
    ROLE_LEFT,       // 자발적 탈퇴
//    ROLE_INIT       // 초기화 (거절,방출,탈퇴) -> 초기화
}
