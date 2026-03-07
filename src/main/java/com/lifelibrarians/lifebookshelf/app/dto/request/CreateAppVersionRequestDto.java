package com.lifelibrarians.lifebookshelf.app.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "app 버전 생성 요청 DTO")
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateAppVersionRequestDto {
    @Schema(description = "앱 버전", example = "1.0.0")
    private final String versionName;

    @Schema(description = "릴리즈 노트", example = "이번 업데이트에서는 여러 가지 버그가 수정되고 새로운 기능이 추가되었습니다.")
    private final String releaseNotes;

    @Schema(description = "강제 업데이트 여부", example = "true")
    private final Boolean isForceUpdate;
}
