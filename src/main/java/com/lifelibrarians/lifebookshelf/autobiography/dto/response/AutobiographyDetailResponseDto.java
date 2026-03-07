package com.lifelibrarians.lifebookshelf.autobiography.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

import lombok.*;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "자서전 상세 정보")
@ToString
public class AutobiographyDetailResponseDto {

	@Schema(description = "자서전 ID", example = "1")
	private final Long autobiographyId;

	@Schema(description = "자서전 챕터 모음")
	private final List<ChapterContent> chapters;

	@Schema(description = "생성일시", example = "2023-01-01T00:00:00Z")
	private final LocalDateTime createdAt;

	@Schema(description = "수정일시", example = "2023-01-02T00:00:00Z")
	private final LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    public static class ChapterContent {
        @Schema(description = "챕터 ID", example = "1")
        private Long chapterId;

        @Schema(description = "챕터 제목", example = "Chapter 1: Childhood")
        private String title;

        @Schema(description = "챕터 내용", example = "This is the content of chapter 1...")
        private String content;

        @Schema(description = "커버 이미지 URL", example = "https://example.com/bio-cover-images/random-string/chapter1.jpg")
        private String coverImageUrl;
    }

}
