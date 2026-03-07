package com.lifelibrarians.lifebookshelf.autobiography.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "자서전과 최신 interview id")
@ToString
public class AutobiographyCurrentResponseDto {
    @Schema(description = "자서전 ID", example = "1")
    private final Long autobiographyId;
}
