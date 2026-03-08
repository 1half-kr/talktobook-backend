package com.lifelibrarians.lifebookshelf.queue.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifelibrarians.lifebookshelf.autobiography.domain.Autobiography;
import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyStatus;
import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyStatusType;
import com.lifelibrarians.lifebookshelf.autobiography.repository.AutobiographyRepository;
import com.lifelibrarians.lifebookshelf.queue.dto.response.AutobiographyMergeResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class AutobiographyMergeConsumer {

    private final AutobiographyRepository autobiographyRepository;
    private final ObjectMapper objectMapper;

    @SqsListener("${sqs.queue.url.autobiography-cycle-merge}")
    @Transactional
    public void receive(String body) {
        try {
            AutobiographyMergeResponseDto dto = objectMapper.readValue(body, AutobiographyMergeResponseDto.class);
            process(dto);
        } catch (Exception e) {
            log.error("[AUTOBIOGRAPHY_MERGE_CONSUMER] 처리 실패: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private void process(AutobiographyMergeResponseDto dto) {
        log.info("[AUTOBIOGRAPHY_MERGE_CONSUMER] 사이클 완료 수신 - cycleId: {}, autobiographyId: {}, userId: {}",
                dto.getCycleId(), dto.getAutobiographyId(), dto.getUserId());

        Autobiography autobiography = autobiographyRepository.findById(dto.getAutobiographyId())
                .orElseThrow(() -> new RuntimeException("Autobiography not found: " + dto.getAutobiographyId()));

        AutobiographyStatus status = autobiography.getAutobiographyStatus();
        status.updateStatusType(AutobiographyStatusType.FINISH, LocalDateTime.now());

        autobiographyRepository.save(autobiography);
        log.info("[AUTOBIOGRAPHY_MERGE_CONSUMER] 자서전 완료 처리 - autobiographyId: {}, cycleId: {}",
                autobiography.getId(), dto.getCycleId());
    }
}
