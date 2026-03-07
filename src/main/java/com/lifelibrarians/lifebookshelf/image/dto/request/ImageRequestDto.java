package com.lifelibrarians.lifebookshelf.image.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@Schema(description = "Presigned-url 발급을 위한 정보 요청")
@ToString
public class ImageRequestDto {

	@Schema(description = "s3에 저장될 파일 경로(ObjectKey)", example = "profile-images/random-string/image.png")
	private String imageUrl;

	@Builder
	ImageRequestDto(final String imageUrl) {
		this.imageUrl = imageUrl;
	}
}
