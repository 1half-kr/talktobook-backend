package com.lifelibrarians.lifebookshelf.publication.dto.request;

import com.lifelibrarians.lifebookshelf.publication.domain.TitlePosition;
import com.lifelibrarians.lifebookshelf.publication.validation.PublicationCreateValidation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@Builder
@Getter
@Schema(description = "출판 생성 요청 DTO")
@ToString
@AllArgsConstructor
@FieldNameConstants
@PublicationCreateValidation
public class PublicationCreateRequestDto {

	@Schema(description = "출판 책 제목", example = "나의 인생")
	private final String title;

	@Schema(description = "사전지정 커버 이미지 URL", example = "book-cover-images/random-string/image.png")
	private final String preSignedCoverImageUrl;

	@Schema(description = "제목 위치", example = "TOP")
	private final TitlePosition titlePosition;
}
