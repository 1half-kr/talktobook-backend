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
public class AutobiographyGenerateRequestDto {

    @Schema(description = "사이클 ID", example = "cycle-12345")
    private final String cycleId;

    @Schema(description = "요청 개수", example = "4")
    private final Integer step;

    @Schema(description = "자서전 ID", example = "1")
    private final Long autobiographyId;

    @Schema(description = "사용자 ID", example = "1")
    private final Long userId;

    @Schema(description = "사용자 정보")
    private final UserInfo userInfo;

    @Schema(description = "자서전 정보")
    private final AutobiographyInfo autobiographyInfo;

    @Schema(description = "인터뷰 답변 목록")
    private final List<InterviewAnswer> answers;

    @Builder
    @AllArgsConstructor
    @Getter
    @Schema(description = "사용자 정보")
    @ToString
    @FieldNameConstants
    public static class UserInfo {
        @Schema(description = "성별", example = "MALE")
        private final String gender;

        @Schema(description = "직업", example = "Software Engineer")
        private final String occupation;

        @Schema(description = "연령대", example = "30")
        private final String ageGroup;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    @Schema(description = "자서전 정보")
    @ToString
    @FieldNameConstants
    public static class AutobiographyInfo {
        @Schema(description = "테마", example = "성장")
        private final String theme;

        @Schema(description = "이유", example = "나의 성장 과정을 기록하고 싶어서")
        private final String reason;

        @Schema(description = "카테고리", example = "개인사")
        private final String category;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    @Schema(description = "인터뷰 답변")
    @ToString
    @FieldNameConstants
    public static class InterviewAnswer {
        @Schema(description = "답변 내용", example = "어린 시절 가장 기억에 남는 순간은...")
        private final String content;

        @Schema(description = "대화 타입", example = "ANSWER")
        private final String conversationType;
    }
}
