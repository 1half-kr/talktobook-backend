package com.lifelibrarians.lifebookshelf.queue.publisher;

import com.lifelibrarians.lifebookshelf.queue.dto.request.InterviewSummaryRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InterviewSummaryPublisher {

    private final QueueMessagingTemplate queueMessagingTemplate;

    @Value("${sqs.queue.url.interview-summary}")
    private String queueUrl;

    public void publishInterviewSummaryRequest(InterviewSummaryRequestDto dto) {
        queueMessagingTemplate.convertAndSend(queueUrl, dto);
    }
}
