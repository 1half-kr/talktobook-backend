package com.lifelibrarians.lifebookshelf.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "인증 코드 재발급 요청 DTO")
@ToString
public class ResendCodeRequestDto {

	@Schema(description = "이메일", example = "example@gmail.com")
	private final String email;
}
