package com.lifelibrarians.lifebookshelf.queue.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "자서전 생성 요청 DTO")
@ToString
@FieldNameConstants
public class InterviewSummaryRequestDto {
    @Schema(description = "인터뷰 ID", example = "1")
    private final Long interviewId;

    @Schema(description = "사용자 ID", example = "1")
    private final Long userId;

    @Schema(description = "인터뷰 답변 목록")
    private final List<Conversations> conversations;

    @Builder
    @AllArgsConstructor
    @Getter
    @Schema(description = "인터뷰 답변")
    @ToString
    @FieldNameConstants
    public static class Conversations {
        @Schema(description = "질문 내용", example = "어린 시절 가장 기억에 남는 순간이 언제인가요 ?")
        private final String question;

        @Schema(description = "답변 내용", example = "어린 시절 가장 기억에 남는 순간은...")
        private final String conversation;
    }
}
