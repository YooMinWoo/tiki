package com.example.tiki.match.dto;

import com.example.tiki.match.domain.enums.MatchStatus;

public enum MatchPostStatusVisible {
    OPEN,
    MATCHED;

    public MatchStatus toMatchStatus() {
        return MatchStatus.valueOf(this.name());
    }
}
