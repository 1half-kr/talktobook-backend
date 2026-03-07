package com.lifelibrarians.lifebookshelf.queue.service;

import com.lifelibrarians.lifebookshelf.autobiography.domain.Autobiography;
import com.lifelibrarians.lifebookshelf.autobiography.repository.AutobiographyRepository;
import com.lifelibrarians.lifebookshelf.interview.domain.ConversationType;
import com.lifelibrarians.lifebookshelf.interview.repository.ConversationRepository;
import com.lifelibrarians.lifebookshelf.queue.dto.request.CycleInitRequestDto;
import com.lifelibrarians.lifebookshelf.queue.publisher.CycleInitPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CycleInitService {
    private final AutobiographyRepository autobiographyRepository;
    private final ConversationRepository conversationRepository;
    private final CycleInitPublisher cycleInitPublisher;

    @Transactional
    public String initializeCycleProcess(Autobiography autobiography) {
        log.info("[INITIALIZE_CYCLE_PROCESS] 사이클 초기화 시작 - autobiographyId: {}, userId: {}", 
                autobiography.getId(), autobiography.getMember().getId());
        
        String cycleId = UUID.randomUUID().toString();
        log.info("[INITIALIZE_CYCLE_PROCESS] Cycle ID 생성 완료 - cycleId: {}", cycleId);
        
        // AutobiographyCompletionService와 동일한 로직으로 expected count 계산
        int expectedCount = calculateExpectedCount(autobiography);
        log.info("[INITIALIZE_CYCLE_PROCESS] Expected count 계산 완료 - cycleId: {}, expectedCount: {}", 
                cycleId, expectedCount);
        
        CycleInitRequestDto cycleInitRequest = CycleInitRequestDto.builder()
                .cycleId(cycleId)
                .expectedCount(expectedCount)
                .autobiographyId(autobiography.getId())
                .userId(autobiography.getMember().getId())
                .build();
                
        cycleInitPublisher.publishAutobiographyCycleInitRequest(cycleInitRequest);
        
        log.info("[INITIALIZE_CYCLE_PROCESS] 사이클 초기화 완료 - cycleId: {}, expectedCount: {}, autobiographyId: {}", 
                cycleId, expectedCount, autobiography.getId());
                
        return cycleId;
    }
    
    private int calculateExpectedCount(Autobiography autobiography) {
        log.info("[CALCULATE_EXPECTED_COUNT] Expected count 계산 시작 - autobiographyId: {}", autobiography.getId());
        
        // HUMAN 타입 conversations에서 카테고리별로 그룹핑하여 개수 계산
        int count = (int) conversationRepository
                .findByAutobiographyId(autobiography.getId())
                .stream()
                .filter(conv -> ConversationType.HUMAN.equals(conv.getConversationType()))
                .filter(conv -> conv.getMaterials() != null && !conv.getMaterials().isEmpty())
                .collect(Collectors.groupingBy(conv -> extractCategoryOrder(conv.getMaterials())))
                .size();
        
        log.info("[CALCULATE_EXPECTED_COUNT] Expected count 계산 완료 - autobiographyId: {}, count: {}", 
                autobiography.getId(), count);
        
        return count;
    }
    
    private Integer extractCategoryOrder(String materials) {
        try {
            String cleaned = materials.trim().replaceAll("\\s+", "");
            if (cleaned.startsWith("[[")) {
                int firstComma = cleaned.indexOf(',');
                if (firstComma > 2) {
                    return Integer.parseInt(cleaned.substring(2, firstComma));
                }
            }
        } catch (Exception e) {
            log.warn("[EXTRACT_CATEGORY_ORDER] 카테고리 순서 추출 실패 - materials: {}", materials);
        }
        return 0;
    }
}
