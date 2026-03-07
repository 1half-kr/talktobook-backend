package com.lifelibrarians.lifebookshelf.queue.consumer;

import com.lifelibrarians.lifebookshelf.autobiography.domain.Autobiography;
import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyStatus;
import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyStatusType;
import com.lifelibrarians.lifebookshelf.autobiography.repository.AutobiographyRepository;
import com.lifelibrarians.lifebookshelf.publication.service.AutobiographyPublicationService;
import com.lifelibrarians.lifebookshelf.queue.dto.response.AutobiographyMergeResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class AutobiographyMergeConsumer {
    private final AutobiographyRepository autobiographyRepository;
    private final AutobiographyPublicationService autobiographyPublicationService;
    
    // 이미 처리된 cycleId 추적
    private final ConcurrentHashMap<String, Boolean> processedCycles = new ConcurrentHashMap<>();

    @RabbitListener(queues = "autobiography.trigger.cycle.merge.queue")
    @Transactional
    public void handleCycleCompletion(AutobiographyMergeResponseDto dto) {
        log.info("[HANDLE_CYCLE_COMPLETION] 사이클 완료 수신 - cycleId: {}, autobiographyId: {}, userId: {}, action: {}",
                dto.getCycleId(), dto.getAutobiographyId(), dto.getUserId(), dto.getAction());

        LocalDateTime now = LocalDateTime.now();

        Autobiography autobiography = autobiographyRepository.findById(dto.getAutobiographyId())
                .orElseThrow(() -> new RuntimeException("Autobiography not found: " + dto.getAutobiographyId()));

        // 자서전 상태를 FINISH로 변경
        AutobiographyStatus status = autobiography.getAutobiographyStatus();
        status.updateStatusType(AutobiographyStatusType.FINISH, now);

        autobiographyRepository.save(autobiography);
        log.info("[HANDLE_CYCLE_COMPLETION] 자서전 병합 완료 - autobiographyId: {}, status: {}, cycleId: {}",
                autobiography.getId(), autobiography.getAutobiographyStatus().getStatus(), dto.getCycleId());
    }
}
