package com.lifelibrarians.lifebookshelf.system.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "약관 응답 DTO")
@ToString
public class TermsResponseDto {

	@Schema(description = "약관 내용", example = "약관 내용입니다.")
	private final String content;

	@Schema(description = "버전", example = "1.0")
	private final String version;
}
