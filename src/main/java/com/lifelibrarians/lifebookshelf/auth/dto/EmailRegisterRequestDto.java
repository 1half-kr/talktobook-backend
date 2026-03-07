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
@Schema(description = "회원가입 요청 DTO")
@ToString
@FieldNameConstants
public class EmailRegisterRequestDto {

	@Schema(description = "이메일", example = "example@gmail.com")
	private final String email;

	@Schema(description = "비밀번호", example = "securepassword")
	private final String password;
}
