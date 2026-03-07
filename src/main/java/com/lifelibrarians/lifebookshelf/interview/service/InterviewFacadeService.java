package com.lifelibrarians.lifebookshelf.interview.service;

import com.lifelibrarians.lifebookshelf.exception.status.InterviewExceptionStatus;
import com.lifelibrarians.lifebookshelf.interview.domain.Interview;

import com.lifelibrarians.lifebookshelf.interview.dto.request.CoShowChatInterviewRequestDto;
import com.lifelibrarians.lifebookshelf.interview.dto.response.CoShowChatInterviewResponseDto;
import com.lifelibrarians.lifebookshelf.interview.dto.response.InterviewConversationResponseDto;
import com.lifelibrarians.lifebookshelf.interview.dto.response.InterviewQuestionResponseDto;
import com.lifelibrarians.lifebookshelf.interview.dto.response.InterviewSummaryOfMonthResponseDto;
import com.lifelibrarians.lifebookshelf.log.Logging;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Logging
public class InterviewFacadeService {

	private final InterviewQueryService interviewQueryService;


	/*-----------------------------------------READ-----------------------------------------*/
	public InterviewConversationResponseDto getConversations(Long memberId, Long interviewId,
			Pageable pageable) {
		return interviewQueryService.getConversations(memberId, interviewId, pageable);
	}

	public InterviewQuestionResponseDto getQuestions(Long memberId, Long interviewId) {
		return interviewQueryService.getQuestions(memberId, interviewId);
	}

    public InterviewSummaryOfMonthResponseDto getInterviewSummaries(Long memberId, Long autobiographyId, Integer year, Integer month) {
        return interviewQueryService.getInterviewSummaries(memberId, autobiographyId, year, month);
    }

    public CoShowChatInterviewResponseDto getCoShowInterviewQuestions(Long interviewId, CoShowChatInterviewRequestDto requestDto) {
        return interviewQueryService.getCoShowInterviewQuestions(interviewId, requestDto);
    }

    public InterviewConversationResponseDto coShowGetConversations(Long interviewId, Pageable pageable) {
        return interviewQueryService.coShowGetConversations(interviewId, pageable);
    }
}
