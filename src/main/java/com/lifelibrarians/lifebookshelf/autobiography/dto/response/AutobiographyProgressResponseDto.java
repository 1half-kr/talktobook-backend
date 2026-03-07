package com.lifelibrarians.lifebookshelf.autobiography.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "자서전 ID와 진행률 응답 DTO")
@ToString
public class AutobiographyProgressResponseDto {
    @Schema(description = "진행률(0~100)", example = "75.5")
    private final Float progressPercentage;

    @Schema(description = "자서전 진행 상태", example = "EMPTY")
    private final String status;
}
