package com.lifelibrarians.lifebookshelf.system.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "피드백 요청 DTO")
@ToString
public class FeedbackRequestDto {

	@Schema(description = "피드백 내용", example = "서비스가 좋습니다.")
	@NotBlank
	private final String content;
}
