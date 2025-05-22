package com.example.tiki.team.dto;

import com.example.tiki.team.domain.enums.TeamStatus;

public enum TeamStatusVisible {
    ACTIVE,
    INACTIVE;

    public TeamStatus toTeamStatus() {
        return TeamStatus.valueOf(this.name());
    }
}
