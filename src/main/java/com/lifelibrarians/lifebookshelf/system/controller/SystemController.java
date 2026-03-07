package com.lifelibrarians.lifebookshelf.system.controller;

import com.lifelibrarians.lifebookshelf.log.Logging;
import com.lifelibrarians.lifebookshelf.system.dto.request.FeedbackRequestDto;
import com.lifelibrarians.lifebookshelf.system.dto.response.TermsResponseDto;
import com.lifelibrarians.lifebookshelf.system.service.SystemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v2/system")
@Tag(name = "시스템 (System)", description = "시스템 관련 API. 서비스 구현 안 됨. DB도 없음")
@Logging
public class SystemController {

	private final SystemService systemService;

	@Operation(summary = "개인정보 처리방침 조회", description = "개인정보 처리방침을 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok"),
	})
	@GetMapping("/terms/privacy")
	public TermsResponseDto getPrivacyPolicy() {
		return systemService.getPrivacyPolicy();
	}

	@Operation(summary = "서비스 이용 약관 조회", description = "서비스 이용약관을 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok"),
	})
	@GetMapping("/terms/service")
	public TermsResponseDto getServiceTerms() {
		return systemService.getServiceTerms();
	}

	@Operation(summary = "피드백 제출", description = "피드백을 제출합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "created"),
	})
	@PostMapping("/feedback")
	@ResponseStatus(HttpStatus.CREATED)
	public void submitFeedback(@Valid @RequestBody FeedbackRequestDto requestDto) {
		systemService.submitFeedback(requestDto);
	}
}
