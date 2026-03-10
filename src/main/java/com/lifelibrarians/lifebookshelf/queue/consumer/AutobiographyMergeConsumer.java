package com.lifelibrarians.lifebookshelf.queue.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifelibrarians.lifebookshelf.autobiography.domain.Autobiography;
import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyStatus;
import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyStatusType;
import com.lifelibrarians.lifebookshelf.autobiography.repository.AutobiographyRepository;
import com.lifelibrarians.lifebookshelf.classification.service.ClassificationInitService;
import com.lifelibrarians.lifebookshelf.interview.domain.Interview;
import com.lifelibrarians.lifebookshelf.interview.repository.InterviewRepository;
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
    private final InterviewRepository interviewRepository;
    private final ClassificationInitService classificationInitService;
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

        LocalDateTime now = LocalDateTime.now();
        
        Autobiography finishedAutobiography = autobiographyRepository.findById(dto.getAutobiographyId())
                .orElseThrow(() -> new RuntimeException("Autobiography not found: " + dto.getAutobiographyId()));

        AutobiographyStatus status = finishedAutobiography.getAutobiographyStatus();
        status.updateStatusType(AutobiographyStatusType.FINISH, now);
        log.info("[AUTOBIOGRAPHY_MERGE_CONSUMER] 기존 자서전 완료 처리 - autobiographyId: {}", finishedAutobiography.getId());

        // 새 자서전 생성
        Autobiography newAutobiography = Autobiography.ofV2(
                null, null, null,
                finishedAutobiography.getTheme(),
                finishedAutobiography.getReason(),
                now, now,
                finishedAutobiography.getMember()
        );
        Autobiography savedNewAutobiography = autobiographyRepository.save(newAutobiography);
        log.info("[AUTOBIOGRAPHY_MERGE_CONSUMER] 새 자서전 생성 완료 - newAutobiographyId: {}", savedNewAutobiography.getId());

        // AutobiographyStatus를 새 자서전으로 변경 및 EMPTY 상태로 설정
        status.updateCurrentAutobiography(savedNewAutobiography);
        status.updateStatusType(AutobiographyStatusType.EMPTY, now);
        log.info("[AUTOBIOGRAPHY_MERGE_CONSUMER] AutobiographyStatus 업데이트 완료 - newAutobiographyId: {}, status: EMPTY", savedNewAutobiography.getId());

        // 분류 체계 초기화
        classificationInitService.initializeFromAiData(savedNewAutobiography);
        log.info("[AUTOBIOGRAPHY_MERGE_CONSUMER] 분류 체계 초기화 완료 - newAutobiographyId: {}", savedNewAutobiography.getId());

        // 새 인터뷰 생성
        Interview newInterview = Interview.ofV2(now, savedNewAutobiography, finishedAutobiography.getMember(), null);
        Interview savedInterview = interviewRepository.save(newInterview);
        log.info("[AUTOBIOGRAPHY_MERGE_CONSUMER] 새 인터뷰 생성 완료 - newInterviewId: {}", savedInterview.getId());

        log.info("[AUTOBIOGRAPHY_MERGE_CONSUMER] 자서전 완료 및 새 자서전 생성 완료 - finishedId: {}, newId: {}, cycleId: {}",
                finishedAutobiography.getId(), savedNewAutobiography.getId(), dto.getCycleId());
    }
}
