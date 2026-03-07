package com.lifelibrarians.lifebookshelf.interview.controller;

import com.lifelibrarians.lifebookshelf.auth.dto.MemberSessionDto;
import com.lifelibrarians.lifebookshelf.auth.jwt.LoginMemberInfo;
import com.lifelibrarians.lifebookshelf.exception.annotation.ApiErrorCodeExample;
import com.lifelibrarians.lifebookshelf.exception.status.CommonExceptionStatus;
import com.lifelibrarians.lifebookshelf.interview.dto.request.CoShowChatInterviewRequestDto;
import com.lifelibrarians.lifebookshelf.interview.dto.response.CoShowChatInterviewResponseDto;
import com.lifelibrarians.lifebookshelf.interview.dto.response.InterviewConversationResponseDto;
import com.lifelibrarians.lifebookshelf.interview.dto.response.InterviewQuestionResponseDto;

import com.lifelibrarians.lifebookshelf.exception.status.InterviewExceptionStatus;

import com.lifelibrarians.lifebookshelf.interview.dto.response.InterviewSummaryOfMonthResponseDto;
import com.lifelibrarians.lifebookshelf.interview.service.InterviewFacadeService;
import com.lifelibrarians.lifebookshelf.log.Logging;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v2/interviews")
@Tag(name = "인터뷰 (Interview)", description = "인터뷰 관련 API")
@Logging
public class InterviewController {

	private final InterviewFacadeService interviewFacadeService;

	@Operation(summary = "인터뷰 대화 조회", description = "인터뷰 대화 내역을 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok"),
	})
	@ApiErrorCodeExample(
			interviewExceptionStatuses = {
					InterviewExceptionStatus.INTERVIEW_NOT_FOUND,
					InterviewExceptionStatus.INTERVIEW_NOT_OWNER,
			}
	)
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{interviewId}/conversations")
	public InterviewConversationResponseDto getInterviewConversations(
			@LoginMemberInfo MemberSessionDto memberSessionDto,
			@PathVariable("interviewId") @Parameter(description = "인터뷰 ID", example = "1") Long interviewId,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size
	) {
		return interviewFacadeService.getConversations(memberSessionDto.getMemberId(), interviewId,
				PageRequest.of(page, size));
	}

	@Operation(summary = "인터뷰 질문 목록 조회", description = "인터뷰 질문 목록을 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok"),
	})
	@ApiErrorCodeExample(
			interviewExceptionStatuses = {
					InterviewExceptionStatus.INTERVIEW_NOT_FOUND,
					InterviewExceptionStatus.INTERVIEW_NOT_OWNER,
			}
	)
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{interviewId}/questions")
	public InterviewQuestionResponseDto getInterviewQuestions(
			@LoginMemberInfo MemberSessionDto memberSessionDto,
			@PathVariable("interviewId") @Parameter(description = "인터뷰 ID", example = "1") Long interviewId
	) {
		return interviewFacadeService.getQuestions(memberSessionDto.getMemberId(), interviewId);
	}

    @Operation(summary = "특정 날짜의 인터뷰 요약 조회", description = "달-일 별로 summary한 conversations(사용자응답)의 갯수와 요약본을 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ok"),
    })
    @ApiErrorCodeExample(
            commonExceptionStatuses = {
                    CommonExceptionStatus.INVALID_YEAR,
                    CommonExceptionStatus.INVALID_MONTH,
            }
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{autobiographyId}/interviews/summaries")
    public InterviewSummaryOfMonthResponseDto getInterviewSummaries(
            @LoginMemberInfo MemberSessionDto memberSessionDto,
            @PathVariable ("autobiographyId") @Parameter(description = "자서전 ID", example = "1") Long autobiographyId,
            @RequestParam("year") @Parameter(description = "년도", example = "2024") Integer year,
            @RequestParam("month") @Parameter(description = "월", example = "12") Integer month
    ) {
        return interviewFacadeService.getInterviewSummaries(memberSessionDto.getMemberId(), autobiographyId, year, month);
    }

    // --------------------------------------------------------
    @Operation(summary = "3. Co-Show 다음 인터뷰 질문 응답", description = "Co-Show - isLast가 true가 되기 전까지 응답을 보내주세요. true가 되면 Co-Show 4번 최종 자서전 생성 요청을 해주세요.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ok"),
    })
    @ApiErrorCodeExample(
            interviewExceptionStatuses = {
                    InterviewExceptionStatus.INTERVIEW_NOT_FOUND,
            }
    )
    @PostMapping(value = "/{interviewId}/co-show/questions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CoShowChatInterviewResponseDto getCoShowInterviewQuestions(
            @PathVariable ("interviewId") @Parameter(description = "인터뷰 ID", example = "1") Long interviewId,
            @Valid @ModelAttribute CoShowChatInterviewRequestDto requestDto) {
        return interviewFacadeService.getCoShowInterviewQuestions(interviewId, requestDto);
    }

    @Operation(summary = "2. Co-Show 채팅 인터뷰 대화 조회", description = "Co-Show용 - 선택한 interviewId에 대한 대화 기록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ok"),
    })
    @ApiErrorCodeExample(
            interviewExceptionStatuses = {
                    InterviewExceptionStatus.INTERVIEW_NOT_FOUND,
            }
    )
    @GetMapping("/{interviewId}/co-show/conversations")
    public InterviewConversationResponseDto coShowGetConversations(
            @PathVariable ("interviewId") @Parameter(description = "인터뷰 ID", example = "1") Long interviewId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return interviewFacadeService.coShowGetConversations(interviewId, PageRequest.of(page, size));
    }
}
