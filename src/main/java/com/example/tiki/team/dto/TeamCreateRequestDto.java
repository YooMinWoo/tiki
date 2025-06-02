package com.example.tiki.team.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamCreateRequestDto {

    @Schema(description = "팀 이름 (3~10자)", example = "레드불", required = true)
    @NotBlank(message = "팀 이름은 필수입니다.")
    @Size(min = 3, max = 10, message = "팀 이름은 3자 이상 10자 이하로 입력해야 합니다.")
    private String teamName;

    @Schema(description = "팀 소개 (20~300자)", example = "레드불은 열정적인 축구 동호회로, 매주 토요일마다 경기를 진행합니다.", required = true)
    @NotBlank(message = "팀 소개는 필수입니다.")
    @Size(min = 20, max = 300, message = "팀 소개는 20자 이상 300자 이하로 입력해야 합니다.")
    private String teamDescription;

}
