package com.lifelibrarians.lifebookshelf.publication.controller;

import com.lifelibrarians.lifebookshelf.auth.dto.MemberSessionDto;
import com.lifelibrarians.lifebookshelf.auth.jwt.LoginMemberInfo;
import com.lifelibrarians.lifebookshelf.exception.status.CommunityExceptionStatus;
import com.lifelibrarians.lifebookshelf.exception.annotation.ApiErrorCodeExample;
import com.lifelibrarians.lifebookshelf.log.Logging;
import com.lifelibrarians.lifebookshelf.publication.dto.request.PublicationCreateRequestDto;
import com.lifelibrarians.lifebookshelf.publication.dto.response.PublicationListResponseDto;
import com.lifelibrarians.lifebookshelf.publication.dto.response.PublicationProgressResponseDto;
import com.lifelibrarians.lifebookshelf.exception.status.PublicationExceptionStatus;
import com.lifelibrarians.lifebookshelf.publication.service.AutobiographyPublicationService;
import com.lifelibrarians.lifebookshelf.publication.service.PublicationFacadeService;
import com.lowagie.text.DocumentException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v2/publications")
@Tag(name = "출판 (Publication)", description = "출판 관련 API")
@Logging
public class PublicationController {

	private final PublicationFacadeService publicationFacadeService;
	private final AutobiographyPublicationService autobiographyPublicationService;

	@Operation(summary = "자신의 출판 목록 조회", description = "자신의 출판 목록을 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok"),
	})
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/me")
	public PublicationListResponseDto getMyPublications(
			@LoginMemberInfo MemberSessionDto memberSessionDto,
			@Parameter(description = "페이지 번호", example = "0") int page,
			@Parameter(description = "페이지 크기", example = "10") int size
	) {
		return publicationFacadeService.getMyPublications(memberSessionDto.getMemberId(),
				PageRequest.of(page, size));
	}

	@Operation(summary = "출판 요청", description = "출판을 요청합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "created")
	})
	@ApiErrorCodeExample(
			publicationExceptionStatuses = {
					PublicationExceptionStatus.PUBLICATION_TITLE_LENGTH_EXCEEDED
			}
	)
	@PreAuthorize("isAuthenticated()")
	@PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public void requestPublication(
			@LoginMemberInfo MemberSessionDto memberSessionDto,
			@Valid @ModelAttribute PublicationCreateRequestDto requestDto
	) {
		publicationFacadeService.requestPublication(memberSessionDto.getMemberId(), requestDto);
	}

	@Operation(summary = "출판 진행상황 조회", description = "출판 진행상황을 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok"),
	})
	@ApiErrorCodeExample(
			publicationExceptionStatuses = {
					PublicationExceptionStatus.PUBLICATION_NOT_FOUND,
					PublicationExceptionStatus.PUBLICATION_NOT_OWNER,
			}
	)
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{publicationId}/progress")
	public PublicationProgressResponseDto getPublicationProgress(
			@LoginMemberInfo MemberSessionDto memberSessionDto,
			@PathVariable("publicationId") @Parameter(description = "출판 ID", example = "1") Long publicationId
	) {
		return publicationFacadeService.getPublicationProgress(memberSessionDto.getMemberId(),
				publicationId);
	}

	@Operation(summary = "출판한 책 삭제", description = "출판한 책을 삭제합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok")
	})
	@ApiErrorCodeExample(
			communityExceptionStatuses = {
					CommunityExceptionStatus.BOOK_NOT_FOUND,
					CommunityExceptionStatus.BOOK_NOT_AUTHENTICATED,
			}
	)
	@PreAuthorize("isAuthenticated()")
	@DeleteMapping("/{bookId}")
	public void deleteBook(
			@LoginMemberInfo MemberSessionDto memberSessionDto,
			@PathVariable("bookId") @Parameter(description = "책 ID", example = "1") Long bookId
	) {
		publicationFacadeService.deleteBook(memberSessionDto.getMemberId(), bookId);
	}

	@Operation(summary = "자서전 PDF 생성 및 S3 업로드", description = "자서전 PDF를 생성하여 S3에 업로드하고 공개 URL을 반환합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok")
	})
	@PostMapping("/autobiography/{autobiographyId}/pdf")
	public String downloadAutobiographyPdf(
			@PathVariable("autobiographyId") @Parameter(description = "자서전 ID", example = "1") Long autobiographyId,
            @Valid @ModelAttribute String name
	) throws IOException, DocumentException {
		return autobiographyPublicationService.uploadPdfToS3(autobiographyId, name);
	}
}
