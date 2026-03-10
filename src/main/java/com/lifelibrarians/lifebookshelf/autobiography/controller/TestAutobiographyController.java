package com.lifelibrarians.lifebookshelf.autobiography.controller;

import com.lifelibrarians.lifebookshelf.queue.dto.response.AutobiographyMergeResponseDto;
import com.lifelibrarians.lifebookshelf.queue.publisher.AutobiographyMergePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestAutobiographyController {

    private final AutobiographyMergePublisher autobiographyMergePublisher;

    @PostMapping("/test/autobiography/finish")
    public String finishAutobiography(@RequestParam Long autobiographyId, @RequestParam Long userId) {
        AutobiographyMergeResponseDto dto = new AutobiographyMergeResponseDto(
                null,
                autobiographyId,
                userId
        );
        autobiographyMergePublisher.publish(dto);
        return "Finish signal sent for autobiography: " + autobiographyId;
    }
}
