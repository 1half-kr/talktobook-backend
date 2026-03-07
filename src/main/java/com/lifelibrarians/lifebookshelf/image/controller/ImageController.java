package com.lifelibrarians.lifebookshelf.image.controller;

import com.lifelibrarians.lifebookshelf.exception.annotation.ApiErrorCodeExample;
import com.lifelibrarians.lifebookshelf.image.dto.request.ImageRequestDto;
import com.lifelibrarians.lifebookshelf.image.service.ImageService;
import com.lifelibrarians.lifebookshelf.log.Logging;
import com.lifelibrarians.lifebookshelf.utils.exception.UtilsExceptionStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/images")
@Tag(name = "이미지 (Image)", description = "이미지 관련 API")
@Logging
public class ImageController {

	private final ImageService imageService;

	@PostMapping("/presigned-url")
	@Operation(summary = "이미지 presigned-url 발급", description = "이미지 업로드를 위한 presigned-url을 발급합니다.")
	@ApiErrorCodeExample(
			utilsExceptionStatuses = {
					UtilsExceptionStatus.INVALID_FILE,
					UtilsExceptionStatus.INVALID_FILE_EXTENSION,
					UtilsExceptionStatus.INVALID_FILE_URL
			}
	)
	@PreAuthorize("isAuthenticated()")
	public String getPreSignedUrl(
			@RequestBody ImageRequestDto imageRequestDto
	) {
		return imageService.getPreSignedUrl(imageRequestDto.getImageUrl());
	}
}
