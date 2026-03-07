package com.lifelibrarians.lifebookshelf.queue.publisher;

import com.lifelibrarians.lifebookshelf.queue.dto.request.AutobiographyGenerateRequestDto;
import com.lifelibrarians.lifebookshelf.queue.dto.request.CycleInitRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CycleInitPublisher {
    private final RabbitTemplate rabbitTemplate;

    public void publishAutobiographyCycleInitRequest(CycleInitRequestDto dto) {
        rabbitTemplate.convertAndSend("autobiography.trigger.cycle.init.queue", dto);
    }
}
