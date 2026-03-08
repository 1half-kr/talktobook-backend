package com.lifelibrarians.lifebookshelf.queue.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifelibrarians.lifebookshelf.queue.dto.response.InterviewPayloadResponseDto;
import com.lifelibrarians.lifebookshelf.queue.service.InterviewPersistenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InterviewConsumer {

    private final InterviewPersistenceService interviewPersistenceService;
    private final ObjectMapper objectMapper;

    @SqsListener("${sqs.queue.url.ai-persistence}")
    public void receive(String body) {
        try {
            InterviewPayloadResponseDto dto = objectMapper.readValue(body, InterviewPayloadResponseDto.class);
            interviewPersistenceService.receiveInterviewPayload(dto);
        } catch (Exception e) {
            log.error("[INTERVIEW_CONSUMER] 처리 실패: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
