package com.example.tiki.match.domain.enums;

public enum MatchStatus {
    OPEN,       // 아직 매칭 신청을 받는 게시글
    MATCHED,    // 매칭이 완료되었고, 아직 경기는 진행하지 않음
    UNMATCHED,  // 신청이 없어서 경기 진행을 못 한 게시글
    COMPLETED,  // 매칭된 경기가 실제로 완료된 경우
    CANCELED,  // 매칭 성사 이후, 한 쪽의 취소로 무산된 게시글
    DELETED,    // 글을 삭제함
}
