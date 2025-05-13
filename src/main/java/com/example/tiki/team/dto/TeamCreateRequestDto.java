package com.example.tiki.team.dto;

import com.example.tiki.team.domain.Team;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamCreateRequestDto {

    private String teamName;
    private String teamDescription;

}
