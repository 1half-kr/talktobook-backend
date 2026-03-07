package com.lifelibrarians.lifebookshelf.autobiography.service;

import com.lifelibrarians.lifebookshelf.autobiography.domain.Autobiography;
import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyStatus;
import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyStatusType;
import com.lifelibrarians.lifebookshelf.autobiography.dto.response.*;
import com.lifelibrarians.lifebookshelf.autobiography.repository.AutobiographyRepository;
import com.lifelibrarians.lifebookshelf.autobiography.repository.AutobiographyStatusRepository;
import com.lifelibrarians.lifebookshelf.classification.domain.Category;
import com.lifelibrarians.lifebookshelf.classification.domain.Material;
import com.lifelibrarians.lifebookshelf.classification.domain.Theme;
import com.lifelibrarians.lifebookshelf.classification.repository.MaterialRepository;
import com.lifelibrarians.lifebookshelf.classification.repository.ThemeRepository;
import com.lifelibrarians.lifebookshelf.exception.status.AutobiographyExceptionStatus;
import com.lifelibrarians.lifebookshelf.log.Logging;
import com.lifelibrarians.lifebookshelf.mapper.AutobiographyMapper;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Logging
public class AutobiographyQueryService {

	private final AutobiographyRepository autobiographyRepository;
    private final AutobiographyStatusRepository autobiographyStatusRepository;
    private final MaterialRepository materialRepository;
    private final ThemeRepository themeRepository;
	private final AutobiographyMapper autobiographyMapper;

    public AutobiographyListResponseDto getAutobiographies(Long memberId, List<String> statuses, Pageable pageable) {
        log.info("[GET_AUTOBIOGRAPHIES] 자서전 목록 조회 시작 - memberId: {}, statuses: {}, page: {}", memberId, statuses, pageable.getPageNumber());

        List<AutobiographyStatusType> statusTypes = statuses.stream()
                .map(AutobiographyStatusType::valueOf)
                .collect(Collectors.toList());

        Page<Autobiography> autobiographyPage =
                autobiographyRepository.findByMemberIdAndStatusesPaged(memberId, statusTypes, pageable);

        log.info("[GET_AUTOBIOGRAPHIES] 자서전 조회 완료 - memberId: {}, totalElements: {}", memberId, autobiographyPage.getTotalElements());

        List<AutobiographyPreviewDto> dtoList =
                autobiographyPage.getContent().stream()
                        .map(a -> autobiographyMapper.toAutobiographyPreviewDto(
                                a,
                                a.getAutobiographyStatus()   // 그냥 이거 한 줄이면 됨
                        ))
                        .collect(Collectors.toList());

        return AutobiographyListResponseDto.builder()
                .results(dtoList)
                .build();
    }

	public AutobiographyDetailResponseDto getAutobiography(Long memberId, Long autobiographyId) {
		log.info("[GET_AUTOBIOGRAPHY] 자서전 상세 조회 시작 - memberId: {}, autobiographyId: {}", memberId, autobiographyId);
		
		Autobiography autobiography = autobiographyRepository.findWithInterviewById(autobiographyId)
				.orElseThrow(
						AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND::toServiceException);
		
		if (!autobiography.getMember().getId().equals(memberId)) {
			log.warn("[GET_AUTOBIOGRAPHY] 자서전 소유자 불일치 - memberId: {}, ownerId: {}", memberId, autobiography.getMember().getId());
			throw AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_OWNER.toServiceException();
		}
		
		log.info("[GET_AUTOBIOGRAPHY] 자서전 상세 조회 완료 - autobiographyId: {}", autobiographyId);
		return autobiographyMapper.toAutobiographyDetailResponseDto(autobiography);
	}

    public AutobiographyCurrentResponseDto getCurrentAutobiography(Long memberId) {
        log.info("[GET_CURRENT_AUTOBIOGRAPHY] 현재 진행중인 자서전 조회 시작 - memberId: {}", memberId);
        
        AutobiographyStatus status = autobiographyStatusRepository
                .findTopByMemberIdAndStatusInOrderByUpdatedAtDesc(
                        memberId,
                        List.of(AutobiographyStatusType.EMPTY, AutobiographyStatusType.PROGRESSING, AutobiographyStatusType.ENOUGH)
                )
                .orElseThrow(AutobiographyExceptionStatus.AUTOBIOGRAPHY_STATUS_NOT_FOUND::toServiceException);

        log.info("[GET_CURRENT_AUTOBIOGRAPHY] 현재 자서전 조회 완료 - memberId: {}, autobiographyId: {}, status: {}", 
                memberId, status.getCurrentAutobiography().getId(), status.getStatus());
        
        return autobiographyMapper.toAutobiographyCurrentResponseDto(status.getCurrentAutobiography());
    }

    public AutobiographyMaterialsResponseDto getAutobiographyMaterials(Long memberId, Long autobiographyId, String sort, Pageable pageable) {
        log.info("[GET_AUTOBIOGRAPHY_MATERIALS] 자서전 소재 조회 시작 - memberId: {}, autobiographyId: {}, sort: {}, page: {}", 
                memberId, autobiographyId, sort, pageable.getPageNumber());
        
        // 1. 자서전 유효성 검증
        Autobiography autobiography = autobiographyRepository.findById(autobiographyId)
                .orElseThrow(AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND::toServiceException);
        
        if (!autobiography.getMember().getId().equals(memberId)) {
            log.warn("[GET_AUTOBIOGRAPHY_MATERIALS] 자서전 소유자 불일치 - memberId: {}, ownerId: {}", memberId, autobiography.getMember().getId());
            throw AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_OWNER.toServiceException();
        }

        // 2. 전체 Material 조회 및 정렬 (DB에서 정렬해도 되지만 이건 전체조회 후 랭킹 매기기 위한 단계로 가정)
        List<Material> allMaterials = materialRepository.findAllByAutobiographyIdOrderByCountDesc(autobiographyId);
        log.info("[GET_AUTOBIOGRAPHY_MATERIALS] 소재 조회 완료 - autobiographyId: {}, totalMaterials: {}", autobiographyId, allMaterials.size());

        // 3. DTO 변환 + rank 세팅
        AtomicInteger rankCounter = new AtomicInteger(1);
        List<AutobiographyMaterialResponseDto> allRankedDtos = allMaterials.stream()
                .sorted(Comparator.comparing(Material::getCount).reversed()) // count 기준 정렬
                .map(material -> {
                    AutobiographyMaterialResponseDto dto = autobiographyMapper.toAutobiographyMaterialResponseDto(material);
                    dto.setRank(rankCounter.getAndIncrement()); // rank 할당
                    return dto;
                })
                .collect(Collectors.toList());

        int totalSize = allRankedDtos.size();

        // 4. 페이지네이션 계산
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), allRankedDtos.size());
        // fromIndex > size 예외 방지
        List<AutobiographyMaterialResponseDto> pageContent =
                (start >= totalSize) ? Collections.emptyList() : allRankedDtos.subList(start, end);

        // 5. Page 객체로 래핑
        Page<AutobiographyMaterialResponseDto> page =
                new PageImpl<>(pageContent, pageable, totalSize);

        log.info("[GET_AUTOBIOGRAPHY_MATERIALS] 소재 조회 완료 - autobiographyId: {}, pageSize: {}", autobiographyId, page.getContent().size());

        // 6. 최종 응답
        return AutobiographyMaterialsResponseDto.builder()
                .popularMaterials(page.getContent())
                .currentPage(page.getNumber())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .isLast(page.isLast())
                .build();
    }

    public AutobiographyProgressResponseDto getAutobiographyProgress(Long memberId, Long autobiographyId) {
        log.info("[GET_AUTOBIOGRAPHY_PROGRESS] 자서전 진행률 조회 시작 - memberId: {}, autobiographyId: {}", memberId, autobiographyId);
        
        Autobiography autobiography = autobiographyRepository.findById(autobiographyId)
                .orElseThrow(AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND::toServiceException);
        
        if (!autobiography.getMember().getId().equals(memberId)) {
            log.warn("[GET_AUTOBIOGRAPHY_PROGRESS] 자서전 소유자 불일치 - memberId: {}, ownerId: {}", memberId, autobiography.getMember().getId());
            throw AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_OWNER.toServiceException();
        }

        // 자서전 진행 상태 progress 반환
        // TODO: 실제 진행 상태 계산 로직 구현 필요
        float progress = 21.7f;

        log.info("[GET_AUTOBIOGRAPHY_PROGRESS] 자서전 진행률 조회 완료 - autobiographyId: {}, progress: {}, status: {}", 
                autobiographyId, progress, autobiography.getAutobiographyStatus().getStatus());

        return AutobiographyProgressResponseDto.builder()
                .progressPercentage(progress)
                .status(autobiography.getAutobiographyStatus().getStatus().name())
                .build();
    }

    // 자서전 id에 대한 사용자의 theme 조회
    public AutobiographyThemeResponseDto getAutobiographyTheme(Long memberId, Long autobiographyId) {
        log.info("[GET_AUTOBIOGRAPHY_THEME] 자서전 테마 조회 시작 - memberId: {}, autobiographyId: {}", memberId, autobiographyId);
        
        Autobiography autobiography = autobiographyRepository.findById(autobiographyId)
                .orElseThrow(AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND::toServiceException);
        
        if (!autobiography.getMember().getId().equals(memberId)) {
            log.warn("[GET_AUTOBIOGRAPHY_THEME] 자서전 소유자 불일치 - memberId: {}, ownerId: {}", memberId, autobiography.getMember().getId());
            throw AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_OWNER.toServiceException();
        }

        // theme name으로 실제 Theme 엔티티 조회
        if (autobiography.getTheme() != null) {
            try {
                Theme theme = themeRepository.findOneByName(autobiography.getTheme().toLowerCase())
                        .orElse(null);

                if (theme != null) {
                    List<Integer> categoryOrders = theme.getCategories().stream()
                            .map(Category::getOrder)
                            .collect(Collectors.toList());

                    log.info("[GET_AUTOBIOGRAPHY_THEME] 자서전 테마 조회 완료 - autobiographyId: {}, theme: {}, categories: {}", 
                            autobiographyId, autobiography.getTheme(), categoryOrders.size());

                    return AutobiographyThemeResponseDto.builder()
                            .name(autobiography.getTheme())
                            .categories(categoryOrders)
                            .build();
                }
            } catch (IllegalArgumentException e) {
                log.warn("[GET_AUTOBIOGRAPHY_THEME] 테마를 찾을 수 없음 - autobiographyId: {}, theme: {}", autobiographyId, autobiography.getTheme());
                throw AutobiographyExceptionStatus.THEME_NOT_FOUND.toServiceException();
            }
        }
        
        log.info("[GET_AUTOBIOGRAPHY_THEME] 테마 없음 - autobiographyId: {}", autobiographyId);
        // theme이 없거나 찾을 수 없는 경우
        return AutobiographyThemeResponseDto.builder()
                .name(autobiography.getTheme())
                .categories(Collections.emptyList())
                .build();
    }

    // CoShow용 Query Services
    // =====================================================================================
    public AutobiographyProgressResponseDto coShowGetAutobiographyProgress(Long autobiographyId) {
        log.info("[GET_AUTOBIOGRAPHY_PROGRESS] 자서전 진행률 조회 시작 - autobiographyId: {}", autobiographyId);

        Autobiography autobiography = autobiographyRepository.findById(autobiographyId)
                .orElseThrow(AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND::toServiceException);

        // 자서전 진행 상태 progress를 임시로 반환
        float progress = 0.0f;

        log.info("[GET_AUTOBIOGRAPHY_PROGRESS] 자서전 진행률 조회 완료 - autobiographyId: {}, progress: {}, status: {}",
                autobiographyId, progress, autobiography.getAutobiographyStatus().getStatus());

        return AutobiographyProgressResponseDto.builder()
                .progressPercentage(progress)
                .status(autobiography.getAutobiographyStatus().getStatus().name())
                .build();
    }
}
