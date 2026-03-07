package com.lifelibrarians.lifebookshelf.queue.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AutobiographyMergeResponseDto {
    private String cycleId;
    private Integer step;
    private Long autobiographyId;
    private Long userId;
    private String action;
    private String title;
    private String content;
}

