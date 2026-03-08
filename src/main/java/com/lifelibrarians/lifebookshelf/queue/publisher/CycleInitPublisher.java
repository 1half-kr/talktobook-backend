package com.lifelibrarians.lifebookshelf.queue.publisher;

import com.lifelibrarians.lifebookshelf.queue.dto.request.CycleInitRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CycleInitPublisher {

    private final QueueMessagingTemplate queueMessagingTemplate;

    @Value("${sqs.queue.url.autobiography-cycle-init}")
    private String queueUrl;

    public void publishAutobiographyCycleInitRequest(CycleInitRequestDto dto) {
        queueMessagingTemplate.convertAndSend(queueUrl, dto);
    }
}
