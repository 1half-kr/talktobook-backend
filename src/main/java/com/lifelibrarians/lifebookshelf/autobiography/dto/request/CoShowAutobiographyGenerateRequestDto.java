package com.lifelibrarians.lifebookshelf.autobiography.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@Builder
@Getter
@Schema(description = "자서전 생성 요청 DTO")
@ToString
@FieldNameConstants
public class CoShowAutobiographyGenerateRequestDto {
    @Schema(description = "이름", example = "정은지")
    private final String name;
}
