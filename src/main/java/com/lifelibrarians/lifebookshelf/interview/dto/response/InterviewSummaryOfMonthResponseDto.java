package com.lifelibrarians.lifebookshelf.interview.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "월별 인터뷰 요약 리스트 응답 DTO")
@ToString
public class InterviewSummaryOfMonthResponseDto {
    @Schema(description = "인터뷰 목록")
    private final List<InterviewSummaryDto> interviews;

    @Builder
    @AllArgsConstructor
    @Getter
    @Schema(description = "인터뷰 요약 정보")
    @ToString
    public static class InterviewSummaryDto {

        @Schema(description = "인터뷰 ID", example = "1")
        private final Long id;

        @Schema(description = "마지막 페이지", example = "5")
        private final Integer totalMessageCount;

        @Schema(description = "요약", example = "오늘은 어린 시절에 대해 이야기했습니다.")
        private final String summary;

        @Schema(description = "총 답변 수", example = "10")
        private final Integer totalAnswerCount;

        @Schema(description = "인터뷰 날짜", example = "2023-10-15")
        private final String date;
    }
}