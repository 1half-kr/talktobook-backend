package com.lifelibrarians.lifebookshelf.autobiography.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "자서전 챕터 정보")
@ToString
public class ChapterDto {

	@Schema(description = "챕터 ID", example = "1")
	private final Long chapterId;

	@Schema(description = "챕터 번호", example = "1")
	private final String chapterNumber;

	@Schema(description = "챕터 이름", example = "Chapter 1: Early Life")
	private final String chapterName;

	@Schema(description = "챕터 설명", example = "This chapter is about my early life.")
	private final String chapterDescription;

	@Schema(description = "챕터 생성 날짜", example = "2023-01-01T00:00:00")
	private final LocalDateTime chapterCreatedAt;

	@ArraySchema(schema = @Schema(implementation = com.lifelibrarians.lifebookshelf.autobiography.dto.response.SubchapterDto.class))
	private final List<SubchapterDto> subChapters;
}