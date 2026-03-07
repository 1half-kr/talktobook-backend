package com.lifelibrarians.lifebookshelf.queue.publisher;

import com.lifelibrarians.lifebookshelf.queue.dto.request.AutobiographyGenerateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AutobiographyGeneratePublisher {
    private final RabbitTemplate rabbitTemplate;

    public void publishGenerateAutobiographyRequest(AutobiographyGenerateRequestDto dto) {
        rabbitTemplate.convertAndSend("autobiography.trigger.queue", dto);
    }
}
