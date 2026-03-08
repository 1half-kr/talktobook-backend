package com.lifelibrarians.lifebookshelf.queue.consumer;

import com.lifelibrarians.lifebookshelf.queue.dto.response.CategoriesPayloadResponseDto;
import com.lifelibrarians.lifebookshelf.queue.service.CategoriesPersistenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoriesConsumer {

    private final CategoriesPersistenceService categoriesPersistenceService;

    @SqsListener("${sqs.queue.url.interview-meta}")
    public void receive(@Payload CategoriesPayloadResponseDto dto) {
        categoriesPersistenceService.receiveCategoriesPayload(dto);
    }
}
