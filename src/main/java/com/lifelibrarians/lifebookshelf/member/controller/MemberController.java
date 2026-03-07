package com.lifelibrarians.lifebookshelf.member.controller;

import com.lifelibrarians.lifebookshelf.auth.dto.MemberSessionDto;
import com.lifelibrarians.lifebookshelf.auth.jwt.LoginMemberInfo;
import com.lifelibrarians.lifebookshelf.exception.annotation.ApiErrorCodeExample;
import com.lifelibrarians.lifebookshelf.log.Logging;
import com.lifelibrarians.lifebookshelf.member.dto.request.MemberUpdateRequestDto;
import com.lifelibrarians.lifebookshelf.member.dto.response.MemberBasicResponseDto;
import com.lifelibrarians.lifebookshelf.exception.status.MemberExceptionStatus;
import com.lifelibrarians.lifebookshelf.member.service.MemberFacadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v2/members")
@Tag(name = "회원 (Member)", description = "회원 관련 API")
@Logging
public class MemberController {

	private final MemberFacadeService memberFacadeService;

	@Operation(summary = "회원 정보 조회", description = "회원 메타데이터를 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok"),
	})
	@ApiErrorCodeExample(
			memberExceptionStatuses = {
					MemberExceptionStatus.MEMBER_METADATA_NOT_FOUND
			}
	)
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/me")
	public MemberBasicResponseDto getMember(
			@LoginMemberInfo MemberSessionDto memberSessionDto
	) {
		return memberFacadeService.getMember(memberSessionDto.getMemberId());
	}

	@Operation(summary = "회원 정보 수정 요청", description = "회원 메타데이터에 대한 수정을 요청합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok"),
	})
	@ApiErrorCodeExample(
			memberExceptionStatuses = {
					MemberExceptionStatus.MEMBER_NAME_LENGTH_EXCEEDED
			}
	)
	@PreAuthorize("isAuthenticated()")
	@PutMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public void updateMember(
			@LoginMemberInfo MemberSessionDto memberSessionDto,
			@Valid @ModelAttribute MemberUpdateRequestDto requestDto
	) {
		memberFacadeService.updateMember(memberSessionDto.getMemberId(), requestDto);
	}
}
