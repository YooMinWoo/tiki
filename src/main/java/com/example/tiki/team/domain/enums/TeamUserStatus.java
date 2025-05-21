package com.example.tiki.team.domain.enums;

public enum TeamUserStatus {
    WAITING,   // 승인 대기
    APPROVED,        // 승인됨
    REJECTED,  // 승인 거절
    KICKED,    // 방출
    LEFT,       // 자발적 탈퇴
//    INIT       // 초기화 (거절,방출,탈퇴) -> 초기화
}
