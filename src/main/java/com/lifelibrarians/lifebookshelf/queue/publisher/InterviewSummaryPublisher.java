package com.lifelibrarians.lifebookshelf.queue.publisher;

import com.lifelibrarians.lifebookshelf.queue.dto.request.InterviewSummaryRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InterviewSummaryPublisher {
    private final RabbitTemplate rabbitTemplate;

    public void publishInterviewSummaryRequest(InterviewSummaryRequestDto dto) {
        rabbitTemplate.convertAndSend("interview.summary.queue", dto);
    }
}
