package com.lifelibrarians.lifebookshelf.autobiography.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "초기 자서전 id와 Interview id 응답 DTO")
@ToString
public class AutobiographyInitResponseDto {
    @Schema(description = "자서전 ID", example = "1")
    private final Long autobiographyId;

    @Schema(description = "인터뷰 ID", example = "1")
    private final Long interviewId;

}
