package com.lifelibrarians.lifebookshelf.queue.publisher;

import com.lifelibrarians.lifebookshelf.queue.dto.request.AutobiographyGenerateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AutobiographyGeneratePublisher {

    private final QueueMessagingTemplate queueMessagingTemplate;

    @Value("${sqs.queue.url.autobiography-trigger}")
    private String queueUrl;

    public void publishGenerateAutobiographyRequest(AutobiographyGenerateRequestDto dto) {
        queueMessagingTemplate.convertAndSend(queueUrl, dto);
    }
}
