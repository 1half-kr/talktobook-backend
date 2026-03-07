package com.lifelibrarians.lifebookshelf.publication.dto.response;

import com.lifelibrarians.lifebookshelf.community.book.domain.VisibleScope;
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
@Schema(description = "출판 미리보기 정보")
@ToString
@FieldNameConstants
public class PublicationPreviewDto {

	@Schema(description = "책 ID", example = "1")
	private final Long bookId;

	@Schema(description = "출판 ID", example = "1")
	private final Long publicationId;

	@Schema(description = "제목", example = "나의 첫 출판 책")
	private final String title;

	@Schema(description = "내용 미리보기", example = "This is the story of my early life...")
	private final String contentPreview;

	@Schema(description = "표지 이미지 URL", example = "https://example.com/book-cover-images/random-string/image.jpg")
	private final String coverImageUrl;

	@Schema(description = "공개 범위", example = "PUBLIC")
	private final VisibleScope visibleScope;

	@Schema(description = "페이지 수", example = "142")
	private final int page;

	@Schema(description = "생성일시", example = "2023-01-01T00:00:00Z")
	private final LocalDateTime createdAt;
}
