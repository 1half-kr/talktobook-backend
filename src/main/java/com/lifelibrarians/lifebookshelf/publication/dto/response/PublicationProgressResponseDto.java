package com.lifelibrarians.lifebookshelf.publication.dto.response;

import com.lifelibrarians.lifebookshelf.community.book.domain.VisibleScope;
import com.lifelibrarians.lifebookshelf.publication.domain.PublishStatus;
import com.lifelibrarians.lifebookshelf.publication.domain.TitlePosition;
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
@Schema(description = "출판 진행상황 응답")
@ToString
@FieldNameConstants
public class PublicationProgressResponseDto {

	@Schema(description = "출판 ID", example = "1")
	private final Long publicationId;

	@Schema(description = "책 ID", example = "2")
	private final Long bookId;

	@Schema(description = "제목", example = "나의 두번째 출판 책")
	private final String title;

	@Schema(description = "표지 이미지 URL", example = "https://example.com/cover.jpg")
	private final String coverImageUrl;

	@Schema(description = "공개 범위", example = "PRIVATE")
	private final VisibleScope visibleScope;

	@Schema(description = "페이지 수", example = "104")
	private final int page;

	@Schema(description = "생성일", example = "2023-01-02T00:00:00Z")
	private final LocalDateTime createdAt;

	@Schema(description = "가격", example = "100000")
	private final int price;

	@Schema(description = "제목 위치", example = "TOP")
	private final TitlePosition titlePosition;

	@Schema(description = "출판 상태", example = "REQUESTED")
	private final PublishStatus publishStatus;

	@Schema(description = "요청일", example = "2023-01-03T00:00:00Z")
	private final LocalDateTime requestedAt;

	@Schema(description = "출판 예정일", example = "2023-01-16T00:00:00Z")
	private final LocalDateTime willPublishedAt;
}
