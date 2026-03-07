package com.lifelibrarians.lifebookshelf.interview.service;

import com.lifelibrarians.lifebookshelf.exception.status.CommonExceptionStatus;
import com.lifelibrarians.lifebookshelf.exception.status.InterviewExceptionStatus;
import com.lifelibrarians.lifebookshelf.interview.domain.Conversation;
import com.lifelibrarians.lifebookshelf.interview.domain.ConversationType;
import com.lifelibrarians.lifebookshelf.interview.domain.Interview;
import com.lifelibrarians.lifebookshelf.interview.domain.InterviewQuestion;
import com.lifelibrarians.lifebookshelf.interview.dto.request.CoShowChatInterviewRequestDto;
import com.lifelibrarians.lifebookshelf.interview.dto.response.*;
import com.lifelibrarians.lifebookshelf.interview.repository.ConversationRepository;
import com.lifelibrarians.lifebookshelf.interview.repository.InterviewQuestionRepository;
import com.lifelibrarians.lifebookshelf.interview.repository.InterviewRepository;
import com.lifelibrarians.lifebookshelf.log.Logging;
import com.lifelibrarians.lifebookshelf.mapper.InterviewMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Logging
public class InterviewQueryService {

	private final InterviewRepository interviewRepository;
    private final InterviewQuestionRepository interviewQuestionRepository;
	private final ConversationRepository conversationRepository;
	private final InterviewMapper interviewMapper;

	public Interview getInterview(Long interviewId) {
		log.info("[GET_INTERVIEW] 인터뷰 조회 시작 - interviewId: {}", interviewId);
		
		Interview interview = interviewRepository.findWithQuestionsById(interviewId)
				.orElseThrow(InterviewExceptionStatus.INTERVIEW_NOT_FOUND::toServiceException);
		
		log.info("[GET_INTERVIEW] 인터뷰 조회 완료 - interviewId: {}, questionsCount: {}", interviewId, interview.getQuestions().size());
		return interview;
	}

	public InterviewConversationResponseDto getConversations(Long memberId, Long interviewId, Pageable pageable) {
		log.info("[GET_CONVERSATIONS] 인터뷰 대화 조회 시작 - memberId: {}, interviewId: {}, page: {}", memberId, interviewId, pageable.getPageNumber());

        // 1개만 가져오고 멤버 ID로 소유자 확인까지 같이
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(InterviewExceptionStatus.INTERVIEW_NOT_FOUND::toServiceException);

        if (!interview.getMember().getId().equals(memberId)) {
            log.warn("[GET_CONVERSATIONS] 인터뷰 소유자 불일치 - memberId: {}, ownerId: {}", memberId, interview.getMember().getId());
            throw InterviewExceptionStatus.INTERVIEW_NOT_OWNER.toServiceException();
        }

		Page<Conversation> conversations = conversationRepository.findAllByInterviewId(
				interviewId, pageable);
		
		log.info("[GET_CONVERSATIONS] 대화 조회 완료 - interviewId: {}, totalElements: {}", interviewId, conversations.getTotalElements());
		
		List<InterviewConversationDto> conversationDtos = conversations.stream()
				.map(interviewMapper::toInterviewConversationDto)
				.collect(Collectors.toList());
		return interviewMapper.toInterviewConversationResponseDto(
				conversationDtos,
				pageable.getPageNumber(),
				(int) conversations.getTotalElements(),
				conversations.getTotalPages(),
				conversations.hasNext(),
				conversations.hasPrevious()
		);
	}

	public InterviewQuestionResponseDto getQuestions(Long memberId, Long interviewId) {
		log.info("[GET_QUESTIONS] 인터뷰 질문 조회 시작 - memberId: {}, interviewId: {}", memberId, interviewId);

        // 1개만 가져오고 멤버 ID로 소유자 확인까지 같이
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(InterviewExceptionStatus.INTERVIEW_NOT_FOUND::toServiceException);

        if (!interview.getMember().getId().equals(memberId)) {
            log.warn("[GET_QUESTIONS] 인터뷰 소유자 불일치 - memberId: {}, ownerId: {}", memberId, interview.getMember().getId());
            throw InterviewExceptionStatus.INTERVIEW_NOT_OWNER.toServiceException();
        }

        List<InterviewQuestion> questions = interview.getQuestions();
        log.info("[GET_QUESTIONS] 질문 목록 조회 완료 - interviewId: {}, questionsCount: {}", interviewId, questions.size());

        // createdAt 기준 가장 최신 질문 선택
        InterviewQuestion currentQuestion = questions.stream()
                .max(Comparator.comparing(InterviewQuestion::getCreatedAt))
                .orElseThrow(InterviewExceptionStatus.INTERVIEW_QUESTION_NOT_FOUND::toServiceException);

        log.info("[GET_QUESTIONS] 현재 질문 선택 완료 - interviewId: {}, currentQuestionId: {}", interviewId, currentQuestion.getId());

        List<InterviewQuestionDto> questionDtos = questions.stream()
                .map(interviewMapper::toInterviewQuestionDto)
                .collect(Collectors.toList());

        return interviewMapper.toInterviewQuestionResponseDto(currentQuestion.getId(), questionDtos);
    }

    public InterviewSummaryOfMonthResponseDto getInterviewSummaries(Long memberId, Long autobiographyId, Integer year, Integer month) {
        log.info("[GET_INTERVIEW_SUMMARIES] 월별 인터뷰 요약 조회 시작 - memberId: {}, autobiographyId: {}, year: {}, month: {}", 
                memberId, autobiographyId, year, month);
        
        if (year < 2000) {
            log.warn("[GET_INTERVIEW_SUMMARIES] 유효하지 않은 연도 - year: {}", year);
            throw CommonExceptionStatus.INVALID_YEAR.toServiceException();
        }
        if (month < 1 || month > 12) {
            log.warn("[GET_INTERVIEW_SUMMARIES] 유효하지 않은 월 - month: {}", month);
            throw CommonExceptionStatus.INVALID_MONTH.toServiceException();
        }

        List<Interview> interviews = interviewRepository.findAllByAutobiographyIdAndYearAndMonth(autobiographyId, year, month);
        log.info("[GET_INTERVIEW_SUMMARIES] 인터뷰 조회 완료 - autobiographyId: {}, interviewsCount: {}", autobiographyId, interviews.size());

        if (!interviews.isEmpty() && !interviews.get(0).getMember().getId().equals(memberId)) {
            log.warn("[GET_INTERVIEW_SUMMARIES] 인터뷰 소유자 불일치 - memberId: {}, ownerId: {}", memberId, interviews.get(0).getMember().getId());
            throw InterviewExceptionStatus.INTERVIEW_NOT_OWNER.toServiceException();
        }

        List<InterviewSummaryOfMonthResponseDto.InterviewSummaryDto> dtoList = interviews.stream()
                .map(interview -> {

                    // total message count는 interviewConversations의 size
                    long totalMessageCount = interview.getInterviewConversations().size();

                    // total answer count는 conversationType이 HUMAN인 것의 개수
                    long totalAnswerCount = interview.getInterviewConversations().stream()
                            .filter(c -> c.getConversationType() == ConversationType.HUMAN)
                            .count();

                    String date = interview.getCreatedAt()
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                    return interviewMapper.toInterviewSummaryDto(
                            interview,
                            (int) totalMessageCount,
                            (int) totalAnswerCount,
                            date
                    );
                })
                .collect(Collectors.toList());

        log.info("[GET_INTERVIEW_SUMMARIES] 월별 인터뷰 요약 조회 완료 - memberId: {}, summariesCount: {}", memberId, dtoList.size());

        return InterviewSummaryOfMonthResponseDto.builder()
                .interviews(dtoList)
                .build();
    }

    // --------------------------------------------------------------
    // CoShow용 질문 반환 api
    @Transactional
    public CoShowChatInterviewResponseDto getCoShowInterviewQuestions(Long interviewId, CoShowChatInterviewRequestDto requestDto) {
        log.info("[GET_COSHOW_INTERVIEW_QUESTIONS] CoShow 인터뷰 질문 조회 시작 - interviewId: {}", interviewId);

        LocalDateTime now = LocalDateTime.now();

        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(InterviewExceptionStatus.INTERVIEW_NOT_FOUND::toServiceException);

        log.info("[GET_COSHOW_INTERVIEW_QUESTIONS] 인터뷰 조회 완료 - interviewId: {}, questionsCount: {}", interviewId, interview.getQuestions().size());

        Integer currentOrder = interview.getCurrentQuestion() != null 
                ? interview.getCurrentQuestion().getQuestionOrder() 
                : null;
        
        if (currentOrder == null) {
            log.warn("[GET_COSHOW_INTERVIEW_QUESTIONS] currentQuestion 없음, 1로 초기화 - interviewId: {}", interviewId);
            currentOrder = 1;
        }

        log.info("[GET_COSHOW_INTERVIEW_QUESTIONS] 현재 질문 order 조회 완료 - currentOrder: {}", currentOrder);

        InterviewQuestion currentQuestion = interviewQuestionRepository.findByInterviewIdAndQuestionOrder(
                interviewId, currentOrder
        ).orElseThrow(
                InterviewExceptionStatus.INTERVIEW_QUESTION_NOT_FOUND::toServiceException
        );

        log.info("[GET_COSHOW_INTERVIEW_QUESTIONS] 현재 질문 선택 완료 - questionId: {}, order: {}", currentQuestion.getId(), currentOrder);

        // 마지막 질문 여부
        Boolean isLast = false;

        // 사용자 응답 저장 (requestDto에 답변이 있는 경우)
        if (requestDto.getAnswerText() != null && !requestDto.getAnswerText().isBlank()) {
            Conversation humanConversation = Conversation.ofV2(
                    requestDto.getAnswerText(),
                    ConversationType.HUMAN,
                    currentQuestion.getMaterials(),
                    interview,
                    now
            );
            conversationRepository.save(humanConversation);
            log.info("[GET_COSHOW_INTERVIEW_QUESTIONS] HUMAN 대화 저장 완료 - conversationId: {}", humanConversation.getId());

            // 다음 질문으로 진행
            Integer nextOrder = currentOrder + 1;

            if (nextOrder <= interview.getQuestions().size()) {
                // 다음 질문 조회
                currentQuestion = interview.getQuestions().stream()
                        .filter(q -> q.getQuestionOrder().equals(nextOrder))
                        .findFirst()
                        .orElseThrow(InterviewExceptionStatus.INTERVIEW_QUESTION_NOT_FOUND::toServiceException);

                interview.setCurrentQuestion(currentQuestion);

                if (nextOrder >= interview.getQuestions().size()) {
                    isLast = true;
                }

            } else {
                isLast = true;
                log.info("[GET_COSHOW_INTERVIEW_QUESTIONS] 모든 질문 완료 - interviewId: {}", interviewId);
            }
        }

        // BOT 응답 저장
        Conversation botConversation = Conversation.ofV2(
                currentQuestion.getQuestionText(),
                ConversationType.BOT,
                currentQuestion.getMaterials(),
                interview,
                now
        );

        conversationRepository.save(botConversation);
        log.info("[GET_COSHOW_INTERVIEW_QUESTIONS] BOT 대화 저장 완료 - conversationId: {}", botConversation.getId());

        CoShowChatInterviewResponseDto response = CoShowChatInterviewResponseDto.builder()
                .id(currentQuestion.getId())
                .order(currentQuestion.getQuestionOrder())
                .question(currentQuestion.getQuestionText())
                .isLast(isLast)
                .build();

        log.info("[GET_COSHOW_INTERVIEW_QUESTIONS] CoShow 인터뷰 질문 조회 완료 - interviewId: {}, currentQuestionId: {}, order: {}", 
                interviewId, currentQuestion.getId(), currentQuestion.getQuestionOrder());
        return response;
    };

    // coShow용 대화 반환 api
    public InterviewConversationResponseDto coShowGetConversations(Long interviewId, Pageable pageable) {
        log.info("[COSHOW_GET_CONVERSATIONS] CoShow 인터뷰 대화 조회 시작 - interviewId: {}, page: {}", interviewId, pageable.getPageNumber());

        Page<Conversation> conversations = conversationRepository.findAllByInterviewId(
                interviewId, pageable);

        log.info("[COSHOW_GET_CONVERSATIONS] 대화 조회 완료 - interviewId: {}, totalElements: {}, pageSize: {}", 
                interviewId, conversations.getTotalElements(), conversations.getContent().size());

        List<InterviewConversationDto> conversationDtos = conversations.stream()
                .map(interviewMapper::toInterviewConversationDto)
                .collect(Collectors.toList());

        return interviewMapper.toInterviewConversationResponseDto(
                conversationDtos,
                pageable.getPageNumber(),
                (int) conversations.getTotalElements(),
                conversations.getTotalPages(),
                conversations.hasNext(),
                conversations.hasPrevious()
        );
    };

//	public Interview getInterviewWithQuestions(Long interviewId) {
//		return interviewRepository.findWithQuestionsById(interviewId)
//				.orElseThrow(InterviewExceptionStatus.INTERVIEW_NOT_FOUND::toServiceException);
//	}
}
