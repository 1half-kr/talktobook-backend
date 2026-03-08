package com.lifelibrarians.lifebookshelf.queue.consumer;

import com.lifelibrarians.lifebookshelf.queue.dto.response.InterviewPayloadResponseDto;
import com.lifelibrarians.lifebookshelf.queue.service.InterviewPersistenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InterviewConsumer {

    private final InterviewPersistenceService interviewPersistenceService;

    @SqsListener("${sqs.queue.url.ai-persistence}")
    public void receive(@Payload InterviewPayloadResponseDto dto) {
        interviewPersistenceService.receiveInterviewPayload(dto);
    }
}
