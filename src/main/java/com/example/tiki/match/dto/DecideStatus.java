package com.example.tiki.match.dto;

import com.example.tiki.match.domain.enums.MatchStatus;
import com.example.tiki.match.domain.enums.RequestStatus;

public enum DecideStatus {
    ACCEPTED,
    REJECTED;

    public RequestStatus toMatchStatus() {
        return RequestStatus.valueOf(this.name());
    }
}
