package com.lifelibrarians.lifebookshelf.autobiography.dto.request;

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
@Schema(description = "자서전 테마, 이유 등록 DTO")
@ToString
@FieldNameConstants
@AutobiographyInitValidation
public class AutobiographyInitRequestDto {

    @Schema(description = "자서전 테마", example = "family")
    private final String theme;

    @Schema(description = "자서전 생성 이유", example = "가족들에게 나의 삶을 정리해서 설명하고 싶어요. 이 자서전은 그 전에 나의 삶을 돌아볼 수 있게 해줄 거에요.")
    private final String reason;
}
