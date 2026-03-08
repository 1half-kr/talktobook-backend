package com.lifelibrarians.lifebookshelf.queue.consumer;

import com.lifelibrarians.lifebookshelf.queue.dto.response.InterviewSummaryResponseDto;
import com.lifelibrarians.lifebookshelf.queue.service.InterviewSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InterviewSummaryConsumer {

    private final InterviewSummaryService interviewSummaryService;

    @SqsListener("${sqs.queue.url.interview-summary-result}")
    public void receive(@Payload InterviewSummaryResponseDto dto) {
        interviewSummaryService.saveInterviewSummary(dto);
    }
}
