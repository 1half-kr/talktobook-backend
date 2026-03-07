package com.lifelibrarians.lifebookshelf.interview.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "인터뷰 질문 응답 정보")
@ToString
@FieldNameConstants
public class InterviewQuestionDto {

	@Schema(description = "질문 ID", example = "1")
	private final Long questionId;

	@Schema(description = "질문 순서", example = "1")
	private final int questionOrder;

	@Schema(description = "질문 내용", example = "What is your name?")
	private final String questionText;
}
