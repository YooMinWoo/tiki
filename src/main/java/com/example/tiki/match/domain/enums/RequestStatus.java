package com.example.tiki.match.domain.enums;

public enum RequestStatus {
    PENDING,        // 처리중
    ACCEPTED,       // 매칭 승낙
    REJECTED,       // 매칭 거절
    CANCELED,        // 매칭 취소 (승낙 이후 취소)
    CANCELED_BY_ADMIN,        // 관리자에 의한 매칭 취소 (승낙 이후 취소)
}