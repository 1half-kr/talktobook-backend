package com.lifelibrarians.lifebookshelf.queue.service;

import com.lifelibrarians.lifebookshelf.autobiography.domain.Autobiography;
import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyStatus;
import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyStatusType;
import com.lifelibrarians.lifebookshelf.classification.repository.CategoryRepository;
import com.lifelibrarians.lifebookshelf.classification.repository.MaterialRepository;
import com.lifelibrarians.lifebookshelf.interview.domain.Conversation;
import com.lifelibrarians.lifebookshelf.interview.domain.ConversationType;
import com.lifelibrarians.lifebookshelf.interview.repository.ConversationRepository;
import com.lifelibrarians.lifebookshelf.publication.service.AutobiographyPublicationService;
import com.lifelibrarians.lifebookshelf.queue.dto.request.AutobiographyGenerateRequestDto;
import com.lifelibrarians.lifebookshelf.queue.publisher.AutobiographyGeneratePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutobiographyCompletionService {

    private final MaterialRepository materialRepository;
    private final CategoryRepository categoryRepository;
    private final ConversationRepository conversationRepository;
    private final AutobiographyGeneratePublisher autobiographyGeneratePublisher;
    private final CycleInitService cycleInitService;
    private final AutobiographyPublicationService autobiographyPublicationService;
    
    private static final double COMPLETION_THRESHOLD = 0.7; // 70%
    private final Map<Long, Boolean> processedAutobiographies = new ConcurrentHashMap<>();

    @Transactional(readOnly = true)
    public boolean checkCompletionAndTriggerPublication(Autobiography autobiography, String name) {
        log.info("[CHECK_COMPLETION] 자서전 완료 체크 시작 - autobiographyId: {}, name: {}", autobiography.getId(), name);

        String safeName = (name == null || name.isBlank()) ? "사용자" : name;

        // 이미 처리된 자서전은 스킵
        if (processedAutobiographies.containsKey(autobiography.getId())) {
            log.info("[CHECK_COMPLETION] 이미 처리된 자서전 - autobiographyId: {}", autobiography.getId());
            return false;
        }

        // 빠른 완료율 체크만 먼저
        double completionRate = calculateCompletionRateQuick(autobiography);
        log.info("[CHECK_COMPLETION] 완료율 계산 완료 - autobiographyId: {}, completionRate: {}%", 
                autobiography.getId(), completionRate * 100);
        
        if (completionRate < COMPLETION_THRESHOLD) {
            log.info("[CHECK_COMPLETION] 완료율 미달 - autobiographyId: {}, required: {}%, current: {}%", 
                    autobiography.getId(), COMPLETION_THRESHOLD * 100, completionRate * 100);
            return false; // 70% 미만이면 바로 리턴
        }

        log.info("[CHECK_COMPLETION] 완료 임계값 도달 - autobiographyId: {}, completionRate: {}%", 
                autobiography.getId(), completionRate * 100);
        
        // 70% 이상일 때만 복잡한 로직 실행
        triggerPublicationRequest(autobiography, safeName);
        processedAutobiographies.put(autobiography.getId(), true);
        
        log.info("[CHECK_COMPLETION] 자서전 완료 체크 완료 - autobiographyId: {}", autobiography.getId());
        return true;
    }

    @Cacheable(value = "completionRate", key = "#autobiography.id + '_' + #autobiography.member.id")
    private double calculateCompletionRateQuick(Autobiography autobiography) {
        log.info("[CALCULATE_COMPLETION_RATE] 완료율 계산 시작 - autobiographyId: {}", autobiography.getId());
        
        Long totalMaterials = materialRepository.countMaterialsByAutobiographyTheme(
            autobiography.getMember().getId(), 
            autobiography.getTheme()
        );
        
        Long completedMaterials = materialRepository.countCompletedMaterialsByAutobiographyTheme(
            autobiography.getMember().getId(), 
            autobiography.getTheme()
        );
        
        log.info("[CALCULATE_COMPLETION_RATE] 소재 개수 조회 완료 - autobiographyId: {}, total: {}, completed: {}", 
                autobiography.getId(), totalMaterials, completedMaterials);
        
        if (totalMaterials == 0) {
            log.warn("[CALCULATE_COMPLETION_RATE] 총 소재 개수가 0 - autobiographyId: {}", autobiography.getId());
            return 0.0;
        }
        
        double rate = (double) completedMaterials / totalMaterials;
        log.info("[CALCULATE_COMPLETION_RATE] 완료율 계산 완료 - autobiographyId: {}, rate: {}%", 
                autobiography.getId(), rate * 100);
        
        return rate;
    }

    public void triggerPublicationRequest(Autobiography autobiography, String name) {
        log.info("[TRIGGER_PUBLICATION] 출판 요청 시작 - autobiographyId: {}, name: {}", autobiography.getId(), name);
        
        LocalDateTime now = LocalDateTime.now();

        // 1. 사이클 초기화 먼저 실행
        String cycleId = cycleInitService.initializeCycleProcess(autobiography);
        log.info("[TRIGGER_PUBLICATION] 사이클 초기화 완료 - cycleId: {}", cycleId);

        // 2. 자서전 생성 요청들 생성
        List<AutobiographyGenerateRequestDto> requests = createGenerateRequests(autobiography, cycleId);
        log.info("[TRIGGER_PUBLICATION] 생성 요청 생성 완료 - requestsCount: {}", requests.size());

        AutobiographyStatus status = autobiography.getAutobiographyStatus();
        status.updateStatusType(AutobiographyStatusType.CREATING, now);
        log.info("[TRIGGER_PUBLICATION] 자서전 상태 변경 - status: CREATING");

        // CoShow용 title 생성
        String coShowExampleTitle = name + "님의 자서전";
        String description = "CoShow 대화로책 부스에 참여해주셔서 감사합니다.";

        autobiography.updateAutoBiography(coShowExampleTitle, description, autobiography.getCoverImageUrl(), now);
        log.info("[TRIGGER_PUBLICATION] 자서전 정보 업데이트 완료 - title: {}", coShowExampleTitle);

        // 3. category order 순으로 순차 발행
        requests.stream()
                .sorted((a, b) -> a.getAutobiographyInfo().getCategory().compareTo(b.getAutobiographyInfo().getCategory()))
                .forEach(autobiographyGeneratePublisher::publishGenerateAutobiographyRequest);
        
        log.info("[TRIGGER_PUBLICATION] 생성 요청 발행 완료 - requestsCount: {}", requests.size());
        
        // PDF 생성은 Consumer에서 모든 챕터 완료 후 수행
        log.info("[TRIGGER_PUBLICATION] PDF는 모든 챕터 완료 후 Consumer에서 생성됨");
        
        log.info("[TRIGGER_PUBLICATION] 출판 요청 완료 - autobiographyId: {}, cycleId: {}", 
                autobiography.getId(), cycleId);
    }

    private List<AutobiographyGenerateRequestDto> createGenerateRequests(Autobiography autobiography, String cycleId) {
        log.info("[CREATE_GENERATE_REQUESTS] 생성 요청 생성 시작 - autobiographyId: {}, cycleId: {}", 
                autobiography.getId(), cycleId);

        // 대화들을 카테고리 오더별로 그룹화
        Map<Integer, List<Conversation>> conversationsByCategory = conversationRepository
                .findByAutobiographyId(autobiography.getId())
                .stream()
                .filter(conv -> conv.getMaterials() != null && !conv.getMaterials().isEmpty())
                .collect(Collectors.groupingBy(
                        conv -> extractCategoryOrder(conv.getMaterials())
                ));

        log.info("[CREATE_GENERATE_REQUESTS] 대화 그룹화 완료 - categoriesCount: {}", conversationsByCategory.size());

        List<AutobiographyGenerateRequestDto> requests = conversationsByCategory.entrySet().stream()
                .map(entry -> createRequestDto(autobiography, cycleId, entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        log.info("[CREATE_GENERATE_REQUESTS] 생성 요청 생성 완료 - requestsCount: {}", requests.size());
        return requests;
    }

    private AutobiographyGenerateRequestDto createRequestDto(Autobiography autobiography, String cycleId, Integer categoryOrder, List<Conversation> conversations) {
        String categoryName = getCategoryName(autobiography.getId(), categoryOrder);
        log.info("[CREATE_REQUEST_DTO] 요청 DTO 생성 - categoryOrder: {}, categoryName: {}, conversationsCount: {}", 
                categoryOrder, categoryName, conversations.size());
        
        return AutobiographyGenerateRequestDto.builder()
                .cycleId(cycleId)
                .step(categoryOrder)
                .autobiographyId(autobiography.getId())
                .userId(autobiography.getMember().getId())
                .userInfo(AutobiographyGenerateRequestDto.UserInfo.builder()
                        .gender(autobiography.getMember().getMemberMemberMetadata().getGender().name())
                        .occupation(autobiography.getMember().getMemberMemberMetadata().getOccupation())
                        .ageGroup(autobiography.getMember().getMemberMemberMetadata().getAgeGroup())
                        .build())
                .autobiographyInfo(AutobiographyGenerateRequestDto.AutobiographyInfo.builder()
                        .theme(autobiography.getTheme())
                        .reason(autobiography.getReason())
                        .category(categoryName)
                        .build())
                .answers(conversations.stream()
                        .map(conv -> AutobiographyGenerateRequestDto.InterviewAnswer.builder()
                                .content(conv.getContent())
                                .conversationType(conv.getConversationType().name())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    private Integer extractCategoryOrder(String materials) {
        // materials에서 첫 번째 배열의 첫 번째 숫자 추출 (예: "[[1, 1, 2], [1, 1, 5]]" -> 1)
        try {
            String cleaned = materials.trim().replaceAll("\\s+", "");
            
            if (cleaned.startsWith("[[")) {
                int firstComma = cleaned.indexOf(',');
                
                if (firstComma > 2) {
                    String firstNumber = cleaned.substring(2, firstComma);
                    return Integer.parseInt(firstNumber);
                } else {
                    log.warn("[EXTRACT_CATEGORY_ORDER] 첫 번째 쉼표 위치가 2 이하 - position: {}", firstComma);
                }
            } else {
                log.warn("[EXTRACT_CATEGORY_ORDER] '[[' 로 시작하지 않음 - materials: {}", materials);
            }
        } catch (Exception e) {
            log.warn("[EXTRACT_CATEGORY_ORDER] 예외 발생 - materials: {}, error: {}", materials, e.getMessage());
        }
        log.warn("[EXTRACT_CATEGORY_ORDER] 기본값 0 반환 - materials: {}", materials);
        return 0;
    }

    private String getCategoryName(Long autobiographyId, Integer categoryOrder) {

        // 1) autobiographyId + order 먼저 조회
        if (autobiographyId != null) {
            Optional<String> opt = categoryRepository
                    .findNameByOrder(autobiographyId, categoryOrder);

            if (opt.isPresent()) {
                return opt.get();
            }
        }

        // 2) autobiographyId 없거나, 데이터 없으면 → order 기반 단 1개 조회
        List<String> fallbackList = categoryRepository.findAnyNameByOrder(categoryOrder);

        if (!fallbackList.isEmpty()) {
            return fallbackList.get(0); // 첫 번째 1개만 사용
        }

        // 3) 그래도 없으면 "default"
        return "default";
    }

    // ----------------------------------------------------------------
    // CoShow용 자서전 생성 트리거 메서드
    public void coShowTriggerPublicationRequest(Autobiography autobiography, String name) {
        log.info("[TRIGGER_PUBLICATION] 출판 요청 시작 - autobiographyId: {}, name: {}", autobiography.getId(), name);

        LocalDateTime now = LocalDateTime.now();

        // 1. 사이클 초기화 먼저 실행
        String cycleId = cycleInitService.initializeCycleProcess(autobiography);
        log.info("[TRIGGER_PUBLICATION] 사이클 초기화 완료 - cycleId: {}", cycleId);

        // 2. 자서전 생성 요청들 생성
        List<AutobiographyGenerateRequestDto> requests = createGenerateRequests(autobiography, cycleId);
        log.info("[TRIGGER_PUBLICATION] 생성 요청 생성 완료 - requestsCount: {}", requests.size());

        AutobiographyStatus status = autobiography.getAutobiographyStatus();
        status.updateStatusType(AutobiographyStatusType.CREATING, now);
        log.info("[TRIGGER_PUBLICATION] 자서전 상태 변경 - status: CREATING");

        // CoShow용 title 생성
        String coShowExampleTitle = name + "님의 자서전";
        String description = "CoShow 대화로책 부스에 참여해주셔서 감사합니다.";

        autobiography.updateAutoBiography(coShowExampleTitle, description, autobiography.getCoverImageUrl(), now);
        log.info("[TRIGGER_PUBLICATION] 자서전 정보 업데이트 완료 - title: {}", coShowExampleTitle);

        // 3. category order 순으로 순차 발행
        requests.stream()
                .sorted((a, b) -> a.getAutobiographyInfo().getCategory().compareTo(b.getAutobiographyInfo().getCategory()))
                .forEach(autobiographyGeneratePublisher::publishGenerateAutobiographyRequest);

        log.info("[TRIGGER_PUBLICATION] 생성 요청 발행 완료 - requestsCount: {}", requests.size());
    }
}
