package com.lifelibrarians.lifebookshelf.queue.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifelibrarians.lifebookshelf.queue.dto.response.CategoriesPayloadResponseDto;
import com.lifelibrarians.lifebookshelf.queue.service.CategoriesPersistenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CategoriesConsumer {

    private final CategoriesPersistenceService categoriesPersistenceService;
    private final ObjectMapper objectMapper;

    @SqsListener("${sqs.queue.url.interview-meta}")
    public void receive(String body) {
        try {
            CategoriesPayloadResponseDto dto = objectMapper.readValue(body, CategoriesPayloadResponseDto.class);
            categoriesPersistenceService.receiveCategoriesPayload(dto);
        } catch (Exception e) {
            log.error("[CATEGORIES_CONSUMER] 처리 실패: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
