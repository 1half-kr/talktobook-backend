package com.lifelibrarians.lifebookshelf.queue.consumer;

import com.lifelibrarians.lifebookshelf.queue.dto.response.InterviewSummaryResponseDto;
import com.lifelibrarians.lifebookshelf.queue.service.InterviewSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InterviewSummaryConsumer {
    private final InterviewSummaryService interviewSummaryService;

    @RabbitListener(queues = "interview.summary.result.queue")
    public void receive(InterviewSummaryResponseDto dto) {
        interviewSummaryService.saveInterviewSummary(dto);
    }
}
