package com.lifelibrarians.lifebookshelf.queue.service;

import com.lifelibrarians.lifebookshelf.interview.domain.Interview;
import com.lifelibrarians.lifebookshelf.interview.repository.InterviewRepository;
import com.lifelibrarians.lifebookshelf.queue.dto.response.InterviewSummaryResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterviewSummaryService {
    private final InterviewRepository interviewRepository;

    @Transactional
    public void saveInterviewSummary(InterviewSummaryResponseDto payload) {
        log.info("[SAVE_INTERVIEW_SUMMARY] 인터뷰 요약 저장 시작 - interviewId: {}", payload.getInterviewId());
        
        // Implementation to save the interview summary
        Optional<Interview> optionalInterview = interviewRepository.findById(payload.getInterviewId());

        if (optionalInterview.isEmpty()) {
            log.warn("[SAVE_INTERVIEW_SUMMARY] 인터뷰를 찾을 수 없음 - interviewId: {}", payload.getInterviewId());
            return;
        }

        Interview interview = optionalInterview.get();

        interview.updateSummary(payload.getSummary());

        interviewRepository.save(interview);
        log.info("[SAVE_INTERVIEW_SUMMARY] 인터뷰 요약 저장 완료 - interviewId: {}, summaryLength: {}", 
                payload.getInterviewId(), payload.getSummary() != null ? payload.getSummary().length() : 0);
    }
}
