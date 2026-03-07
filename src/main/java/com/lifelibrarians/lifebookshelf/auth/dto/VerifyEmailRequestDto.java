package com.lifelibrarians.lifebookshelf.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "이메일 인증 요청 DTO")
@ToString
@FieldNameConstants
public class VerifyEmailRequestDto {

	@Schema(description = "이메일", example = "example@gmail.com")
	private final String email;

	@Schema(description = "인증코드", example = "123456")
	private final String verificationCode;
}
