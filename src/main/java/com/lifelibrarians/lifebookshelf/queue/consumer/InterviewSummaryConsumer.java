package com.lifelibrarians.lifebookshelf.queue.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifelibrarians.lifebookshelf.queue.dto.response.InterviewSummaryResponseDto;
import com.lifelibrarians.lifebookshelf.queue.service.InterviewSummaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InterviewSummaryConsumer {

    private final InterviewSummaryService interviewSummaryService;
    private final ObjectMapper objectMapper;

    @SqsListener("${sqs.queue.url.interview-summary-result}")
    public void receive(String body) {
        try {
            InterviewSummaryResponseDto dto = objectMapper.readValue(body, InterviewSummaryResponseDto.class);
            interviewSummaryService.saveInterviewSummary(dto);
        } catch (Exception e) {
            log.error("[INTERVIEW_SUMMARY_CONSUMER] 처리 실패: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
