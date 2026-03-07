package com.lifelibrarians.lifebookshelf.app.dto.request;

import com.lifelibrarians.lifebookshelf.autobiography.validate.AutobiographyInitValidation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "특정 유저의 앱 버전 설정 요청 DTO")
@ToString
@FieldNameConstants
@AutobiographyInitValidation
public class PatchMemberAppVersionRequestDto {
    @Schema(description = "버전 코드", example = "5")
    private final String versionCode;
}
