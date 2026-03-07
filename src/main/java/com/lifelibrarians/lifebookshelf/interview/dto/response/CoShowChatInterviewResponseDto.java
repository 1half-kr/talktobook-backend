package com.lifelibrarians.lifebookshelf.interview.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "인터뷰 질문 응답 DTO")
@ToString
@FieldNameConstants
public class CoShowChatInterviewResponseDto {

     @Schema(description = "질문 ID", example = "q12345")
     Long id;

     @Schema(description = "질문 순서", example = "1")
     Integer order;

     @Schema(description = "다음 질문", example = "What is your favorite hobby?")
     String question;

     @Schema(description = "종료 여부", example = "true")
     Boolean isLast;
}
