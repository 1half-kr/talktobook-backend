package com.lifelibrarians.lifebookshelf.autobiography.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "자서전 서브챕터 정보")
@ToString
public class SubchapterDto {

	@Schema(description = "서브챕터 ID", example = "3")
	private final Long chapterId;

	@Schema(description = "서브챕터 번호", example = "1.1")
	private final String chapterNumber;

	@Schema(description = "서브챕터 이름", example = "Subchapter 1: Childhood")
	private final String chapterName;

	@Schema(description = "서브챕터 설명", example = "This subchapter is about my childhood.")
	private final String chapterDescription;

	@Schema(description = "서브챕터 생성 날짜", example = "2023-01-01T01:00:00")
	private final LocalDateTime chapterCreatedAt;
}