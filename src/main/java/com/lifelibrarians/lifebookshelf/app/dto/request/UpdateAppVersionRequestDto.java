package com.lifelibrarians.lifebookshelf.app.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "앱 버전 업데이트 요청 DTO")
@ToString
@FieldNameConstants
public class UpdateAppVersionRequestDto {
    @Schema(description = "앱 버전", example = "1.0.0")
    private final String versionName;

    @Schema(description = "강제 업데이트 여부", example = "true")
    private final Boolean isForceUpdate;

    @Schema(description = "릴리즈 노트", example = "이번 업데이트에서는 여러 가지 버그가 수정되고 새로운 기능이 추가되었습니다.")
    private final String releaseNotes;
}
