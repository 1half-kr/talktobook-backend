package com.lifelibrarians.lifebookshelf.interview.dto.request;

import com.lifelibrarians.lifebookshelf.interview.domain.ConversationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "인터뷰 대화 생성 요청 DTO")
@ToString
@FieldNameConstants
public class InterviewConversationDto {

	@Schema(description = "대화 내용", example = "I was born in Seoul.")
	private final String content;

	@Schema(description = "대화 유형", example = "HUMAN")
	private final ConversationType conversationType;
}
