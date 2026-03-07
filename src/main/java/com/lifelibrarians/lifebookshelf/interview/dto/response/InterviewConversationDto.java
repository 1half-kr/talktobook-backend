package com.lifelibrarians.lifebookshelf.interview.dto.response;

import com.lifelibrarians.lifebookshelf.interview.domain.ConversationType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "인터뷰 대화 정보")
@ToString
@FieldNameConstants
public class InterviewConversationDto {

	@Schema(description = "대화 ID", example = "1")
	private final Long conversationId;

	@Schema(description = "대화 내용", example = "Hello, how are you?")
	private final String content;

	@Schema(description = "대화 유형", example = "HUMAN")
	private final ConversationType conversationType;

	@Schema(description = "생성일시", example = "2023-01-01T00:00:00Z")
	private final LocalDateTime createdAt;
}
