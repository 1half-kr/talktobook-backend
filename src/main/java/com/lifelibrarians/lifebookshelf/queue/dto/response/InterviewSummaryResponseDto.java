package com.lifelibrarians.lifebookshelf.queue.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InterviewSummaryResponseDto {
    private Long interviewId;
    private Long userId;
    private String summary;
}
