package com.lifelibrarians.lifebookshelf.mapper;

import com.lifelibrarians.lifebookshelf.interview.domain.Conversation;
import com.lifelibrarians.lifebookshelf.interview.domain.Interview;
import com.lifelibrarians.lifebookshelf.interview.domain.InterviewQuestion;
import com.lifelibrarians.lifebookshelf.interview.dto.response.*;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InterviewMapper {

	@Mapping(source = "conversation.id", target = "conversationId")
	InterviewConversationDto toInterviewConversationDto(Conversation conversation);

	@Mapping(source = "conversationDtos", target = "results")
	InterviewConversationResponseDto toInterviewConversationResponseDto(
			List<InterviewConversationDto> conversationDtos,
			int currentPage,
			int totalElements,
			int totalPages,
			boolean hasNextPage,
			boolean hasPreviousPage
	);

	@Mapping(source = "questionDtos", target = "results")
	InterviewQuestionResponseDto toInterviewQuestionResponseDto(
			Long currentQuestionId,
			List<InterviewQuestionDto> questionDtos
	);

	@Mapping(source = "interviewQuestion.id", target = "questionId")
    @Mapping(source = "interviewQuestion.questionOrder", target = "questionOrder")
    InterviewQuestionDto toInterviewQuestionDto(InterviewQuestion interviewQuestion);

    @Mapping(source = "interview.id", target = "id")
    InterviewSummaryOfMonthResponseDto.InterviewSummaryDto toInterviewSummaryDto(Interview interview, int totalMessageCount, int totalAnswerCount, String date);
}
