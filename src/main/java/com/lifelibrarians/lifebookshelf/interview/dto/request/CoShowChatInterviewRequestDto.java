package com.lifelibrarians.lifebookshelf.interview.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
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
@Schema(description = "인터뷰 응답 전송 DTO")
@ToString
@FieldNameConstants
public class CoShowChatInterviewRequestDto {
    @Schema(description = "응답 내용", example = "I was born in Seoul.")
    private final String answerText;
}
