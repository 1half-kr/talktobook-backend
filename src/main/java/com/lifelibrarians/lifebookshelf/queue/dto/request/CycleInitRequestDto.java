package com.lifelibrarians.lifebookshelf.queue.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "사이클 초기화 요청 DTO")
@ToString
@FieldNameConstants
public class CycleInitRequestDto {
    @Schema(description = "사이클 ID", example = "cycle-12345")
    private final String cycleId;

    @Schema(description = "요청 개수", example = "4")
    private final Integer expectedCount;

    @Schema(description = "자서전 ID", example = "1")
    private final Long autobiographyId;

    @Schema(description = "사용자 ID", example = "1")
    private final Long userId;
}
