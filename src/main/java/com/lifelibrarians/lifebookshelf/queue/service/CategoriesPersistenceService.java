package com.lifelibrarians.lifebookshelf.queue.service;

import com.lifelibrarians.lifebookshelf.autobiography.domain.Autobiography;
import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyStatus;
import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyStatusType;
import com.lifelibrarians.lifebookshelf.autobiography.repository.AutobiographyRepository;
import com.lifelibrarians.lifebookshelf.classification.domain.Chunk;
import com.lifelibrarians.lifebookshelf.classification.repository.ChunkRepository;
import com.lifelibrarians.lifebookshelf.classification.repository.MaterialRepository;
import com.lifelibrarians.lifebookshelf.queue.dto.response.CategoriesPayloadResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoriesPersistenceService {
    private final AutobiographyRepository autobiographyRepository;
    private final ChunkRepository chunkRepository;
    private final MaterialRepository materialRepository;

    private final AutobiographyCompletionService autobiographyCompletionService;

    @Transactional
    public void receiveCategoriesPayload(CategoriesPayloadResponseDto payload) {
        log.info("[RECEIVE_CATEGORIES_PAYLOAD] 카테고리 페이로드 수신 시작 - userId: {}, autobiographyId: {}, categoryId: {}", 
                payload.getUserId(), payload.getAutobiographyId(), payload.getCategoryId());
        
        // 사용자 권한 검증
        Autobiography autobiography = autobiographyRepository.findById(payload.getAutobiographyId())
            .orElseThrow(() -> new RuntimeException("자서전을 찾을 수 없습니다."));
        
        if (!autobiography.getMember().getId().equals(payload.getUserId())) {
            log.warn("[RECEIVE_CATEGORIES_PAYLOAD] 자서전 소유자 불일치 - userId: {}, ownerId: {}", 
                    payload.getUserId(), autobiography.getMember().getId());
            throw new RuntimeException("해당 자서전에 대한 권한이 없습니다.");
        }

        // Chunk 변화량 처리
        if (payload.getChunks() != null) {
            log.info("[RECEIVE_CATEGORIES_PAYLOAD] Chunk 변화량 처리 시작 - chunksCount: {}", payload.getChunks().size());
            
            payload.getChunks().forEach(chunkPayload -> {
                var chunkOpt = chunkRepository.findByAutobiographyAndThemeAndCategoryOrderAndChunkOrder(autobiography.getId(), payload.getThemeId(), payload.getCategoryId(), chunkPayload.getChunkOrder());
                if (chunkOpt.isPresent()) {
                    Chunk chunk = chunkOpt.get();
                    int oldWeight = chunk.getWeight();
                    chunk.updateWeight(oldWeight + chunkPayload.getWeight());
                    chunkRepository.save(chunk);
                    log.info("[RECEIVE_CATEGORIES_PAYLOAD] Chunk 업데이트 완료 - chunkId: {}, oldWeight: {}, newWeight: {}", 
                            chunk.getId(), oldWeight, chunk.getWeight());
                } else {
                    log.warn("[RECEIVE_CATEGORIES_PAYLOAD] Chunk를 찾을 수 없음 - themeId: {}, categoryId: {}, chunkOrder: {}", 
                            payload.getThemeId(), payload.getCategoryId(), chunkPayload.getChunkOrder());
                }
            });
        }

        // Material 변화량 처리
        if (payload.getMaterials() != null) {
            log.info("[RECEIVE_CATEGORIES_PAYLOAD] Material 변화량 처리 시작 - materialsCount: {}", payload.getMaterials().size());
            
            payload.getMaterials().forEach(materialPayload -> {
                materialRepository.findByAutobiographyAndThemeAndOrdersAndMaterialOrder(autobiography.getId(), payload.getThemeId(), payload.getCategoryId(), materialPayload.getChunkId(), materialPayload.getMaterialOrder())
                    .filter(material -> material.getChunk().getCategory().getAutobiography().getId().equals(autobiography.getId()))
                    .ifPresentOrElse(material -> {
                        // 변화량 적용
                        int oldCount = material.getCount();
                        material.updateExample(material.getExample() + materialPayload.getExample());
                        material.updateSimilarEvent(material.getSimilarEvent() + materialPayload.getSimilarEvent());
                        material.updateCount(material.getCount() + materialPayload.getCount());
                        
                        // Principle 배열 업데이트
                        int[] currentPrinciple = parsePrincipleArray(material.getPrinciple());
                        int[] deltaPrinciple = materialPayload.getPrinciple().stream().mapToInt(Integer::intValue).toArray();
                        for (int i = 0; i < Math.min(currentPrinciple.length, deltaPrinciple.length); i++) {
                            currentPrinciple[i] += deltaPrinciple[i];
                        }
                        material.updatePrinciple(Arrays.toString(currentPrinciple));
                        
                        materialRepository.save(material);
                        log.info("[RECEIVE_CATEGORIES_PAYLOAD] Material 업데이트 완료 - materialId: {}, oldCount: {}, newCount: {}", 
                                material.getId(), oldCount, material.getCount());
                    }, () -> {
                        log.warn("[RECEIVE_CATEGORIES_PAYLOAD] Material을 찾을 수 없음 - themeId: {}, categoryId: {}, chunkId: {}, materialOrder: {}", 
                                payload.getThemeId(), payload.getCategoryId(), materialPayload.getChunkId(), materialPayload.getMaterialOrder());
                    });
            });
        }

        String coShowExampleName = "사용자"; // 기본 이름 설정

        // 자서전 완료 여부 체크 및 상태 업데이트
        log.info("[RECEIVE_CATEGORIES_PAYLOAD] 자서전 완료 여부 체크 시작 - autobiographyId: {}", autobiography.getId());
        boolean isEnough = autobiographyCompletionService.checkCompletionAndTriggerPublication(autobiography, coShowExampleName);
        if (isEnough) {
            LocalDateTime now = LocalDateTime.now();
            AutobiographyStatus status = autobiography.getAutobiographyStatus();
            status.updateStatusType(AutobiographyStatusType.ENOUGH, now); // 상태 업데이트
            log.info("[RECEIVE_CATEGORIES_PAYLOAD] 자서전 상태 변경 - autobiographyId: {}, status: ENOUGH", autobiography.getId());
        }
        
        log.info("[RECEIVE_CATEGORIES_PAYLOAD] 카테고리 페이로드 처리 완료 - autobiographyId: {}, categoryId: {}", 
                payload.getAutobiographyId(), payload.getCategoryId());
    }

    private int[] parsePrincipleArray(String principleStr) {
        try {
            return Arrays.stream(principleStr.replace("[", "").replace("]", "").split(","))
                    .mapToInt(s -> Integer.parseInt(s.trim()))
                    .toArray();
        } catch (Exception e) {
            return new int[]{0,0,0,0,0,0}; // 기본값
        }
    }
}
