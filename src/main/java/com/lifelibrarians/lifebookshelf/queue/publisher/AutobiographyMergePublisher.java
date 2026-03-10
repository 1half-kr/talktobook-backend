package com.lifelibrarians.lifebookshelf.queue.publisher;

import com.lifelibrarians.lifebookshelf.queue.dto.response.AutobiographyMergeResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AutobiographyMergePublisher {

    private final QueueMessagingTemplate queueMessagingTemplate;

    @Value("${sqs.queue.url.autobiography-cycle-merge}")
    private String queueUrl;

    public void publish(AutobiographyMergeResponseDto dto) {
        queueMessagingTemplate.convertAndSend(queueUrl, dto);
    }
}
