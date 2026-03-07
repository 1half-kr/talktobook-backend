package com.lifelibrarians.lifebookshelf.queue.service;

import com.lifelibrarians.lifebookshelf.autobiography.domain.Autobiography;
import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyStatusType;
import com.lifelibrarians.lifebookshelf.autobiography.repository.AutobiographyRepository;
import com.lifelibrarians.lifebookshelf.exception.status.AuthExceptionStatus;
import com.lifelibrarians.lifebookshelf.exception.status.AutobiographyExceptionStatus;
import com.lifelibrarians.lifebookshelf.exception.status.InterviewExceptionStatus;
import com.lifelibrarians.lifebookshelf.interview.domain.Conversation;
import com.lifelibrarians.lifebookshelf.interview.domain.ConversationType;
import com.lifelibrarians.lifebookshelf.interview.domain.Interview;
import com.lifelibrarians.lifebookshelf.interview.domain.InterviewQuestion;
import com.lifelibrarians.lifebookshelf.interview.repository.ConversationRepository;
import com.lifelibrarians.lifebookshelf.interview.repository.InterviewQuestionRepository;
import com.lifelibrarians.lifebookshelf.interview.repository.InterviewRepository;
import com.lifelibrarians.lifebookshelf.log.Logging;
import com.lifelibrarians.lifebookshelf.queue.dto.response.InterviewPayloadResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// DB 저장 수행 계층
@Slf4j
@Service
@RequiredArgsConstructor
@Logging
public class InterviewPersistenceService {
    private final InterviewRepository interviewRepository;
    private final InterviewQuestionRepository interviewQuestionRepository;
    private final ConversationRepository conversationRepository;
    private final AutobiographyRepository autobiographyRepository;

    @Transactional
    public void receiveInterviewPayload(InterviewPayloadResponseDto payload) {
        log.info("[RECEIVE_INTERVIEW_PAYLOAD] 인터뷰 페이로드 수신 시작 - userId: {}, autobiographyId: {}", 
                payload.getUserId(), payload.getAutobiographyId());

        // 1. 가장 최근 interview 찾기
        Interview interview = interviewRepository.findTopByAutobiographyIdOrderByCreatedAtDesc(payload.getAutobiographyId())
                .orElseThrow(InterviewExceptionStatus.INTERVIEW_NOT_FOUND::toServiceException);

        log.info("[RECEIVE_INTERVIEW_PAYLOAD] 인터뷰 조회 완료 - interviewId: {}", interview.getId());

        // 만일 해당 유저의 interview가 아닌 경우 오류 출력
        if (!interview.getMember().getId().equals(payload.getUserId())) {
            log.warn("[RECEIVE_INTERVIEW_PAYLOAD] 인터뷰 소유자 불일치 - userId: {}, ownerId: {}", 
                    payload.getUserId(), interview.getMember().getId());
            throw AuthExceptionStatus.MEMBER_NOT_FOUND.toServiceException();
        }

        LocalDateTime now = LocalDateTime.now();

        int nextQuestionOrder = interview.getQuestions().stream()
                .map(InterviewQuestion::getQuestionOrder)
                .max(Integer::compareTo)
                .orElse(0) + 1;

        log.info("[RECEIVE_INTERVIEW_PAYLOAD] 다음 질문 순서 계산 완료 - nextQuestionOrder: {}", nextQuestionOrder);

        // 4. 인터뷰 질문 저장
        InterviewQuestion question = InterviewQuestion.ofV2(
                nextQuestionOrder,
                payload.getInterviewQuestion().getQuestionText(),
                payload.getInterviewQuestion().getMaterials(), // material은 빈 문자열 허용
                now,
                interview
        );

        interviewQuestionRepository.save(question);
        log.info("[RECEIVE_INTERVIEW_PAYLOAD] 인터뷰 질문 저장 완료 - questionId: {}, order: {}", 
                question.getId(), nextQuestionOrder);

        // 3. 인터뷰 응답(Conversation) 저장
        List<Conversation> conversations = payload.getConversation().stream()
                .map(dto -> {
                    ConversationType type = ConversationType.valueOf(dto.getConversationType().toUpperCase());
                    return Conversation.ofV2(
                            dto.getContent(),
                            type,
                            dto.getMaterials(),
                            interview,
                            now
                    );
                })
                .collect(Collectors.toList());

        conversationRepository.saveAll(conversations);
        log.info("[RECEIVE_INTERVIEW_PAYLOAD] 대화 저장 완료 - conversationsCount: {}", conversations.size());

        Autobiography autobiography = autobiographyRepository.findById(payload.getAutobiographyId())
                .orElseThrow(AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND::toServiceException);

        if (autobiography.getAutobiographyStatus().getStatus() == AutobiographyStatusType.EMPTY) {
            log.info("[RECEIVE_INTERVIEW_PAYLOAD] 자서전 상태 변경 - EMPTY -> PROGRESSING, autobiographyId: {}", 
                    payload.getAutobiographyId());
            // 최초 인터뷰 응답이 저장되는 경우, 자서전 상태를 PROGRESSING으로 변경
            autobiography.getAutobiographyStatus().updateStatusType(AutobiographyStatusType.PROGRESSING, now);
        }

        log.info("[RECEIVE_INTERVIEW_PAYLOAD] 인터뷰 페이로드 처리 완료 - autobiographyId: {}, interviewId: {}", 
                payload.getAutobiographyId(), interview.getId());
    }
}
