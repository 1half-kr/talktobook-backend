package com.lifelibrarians.lifebookshelf.autobiography.service;

import com.lifelibrarians.lifebookshelf.autobiography.dto.request.AutobiographyInitRequestDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.request.AutobiographyUpdateRequestDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.request.CoShowAutobiographyGenerateRequestDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.response.*;
import com.lifelibrarians.lifebookshelf.log.Logging;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Logging
public class AutobiographyFacadeService {

	private final AutobiographyQueryService autobiographyQueryService;
	private final AutobiographyCommandService autobiographyCommandService;

    public AutobiographyInitResponseDto initAutobiography(Long memberId, AutobiographyInitRequestDto requestDto) {
        return autobiographyCommandService.initAutobiography(memberId, requestDto);
    }

	public AutobiographyListResponseDto getAutobiographies(Long memberId, List<String> statuses, Pageable pageable) {
		return autobiographyQueryService.getAutobiographies(memberId, statuses, pageable);
	}

    public AutobiographyMaterialsResponseDto getAutobiographyMaterials(Long memberId, Long autobiographyId, String sort, Pageable pageable) {
        return autobiographyQueryService.getAutobiographyMaterials(memberId, autobiographyId, sort, pageable);
    }

	public AutobiographyDetailResponseDto getAutobiography(Long memberId, Long autobiographyId) {
		return autobiographyQueryService.getAutobiography(memberId, autobiographyId);
	}

    public AutobiographyCurrentResponseDto getCurrentAutobiography(Long memberId) {
        return autobiographyQueryService.getCurrentAutobiography(memberId);
    }

    public AutobiographyProgressResponseDto getAutobiographyProgress(Long memberId, Long autobiographyId) {
        return autobiographyQueryService.getAutobiographyProgress(memberId, autobiographyId);
    }

    public AutobiographyThemeResponseDto getAutobiographyTheme(Long memberId, Long autobiographyId) {
        return autobiographyQueryService.getAutobiographyTheme(memberId, autobiographyId);
    }

	public void patchAutobiography(Long memberId, Long autobiographyId, AutobiographyUpdateRequestDto requestDto) {
		autobiographyCommandService.patchAutobiography(memberId, autobiographyId, requestDto);
	}

    public void patchReasonAutobiography(Long memberId, Long autobiographyId, AutobiographyInitRequestDto requestDto) {
        autobiographyCommandService.patchReasonAutobiography(memberId, autobiographyId, requestDto);
    }

    public void requestAutobiographyGenerate(Long memberId, Long autobiographyId, CoShowAutobiographyGenerateRequestDto requestDto) {
        autobiographyCommandService.requestAutobiographyGenerate(memberId, autobiographyId, requestDto);
    }

    public void patchAutobiographyStatus(Long memberId, Long autobiographyId, String status) {
        autobiographyCommandService.patchAutobiographyStatus(memberId, autobiographyId, status);
    }

	public void deleteAutobiography(Long memberId, Long autobiographyId) {
		autobiographyCommandService.deleteAutobiography(memberId, autobiographyId);
	}

    // ----------------------------------------------------
    // CoShow용
    public void coShowRequestAutobiographyGenerate(Long autobiographyId, CoShowAutobiographyGenerateRequestDto requestDto) {
        autobiographyCommandService.coShowRequestAutobiographyGenerate(autobiographyId, requestDto);
    }

    public void coShowNewRequestAutobiographyGenerate(Long autobiographyId, CoShowAutobiographyGenerateRequestDto requestDto) {
        autobiographyCommandService.coShowNewRequestAutobiographyGenerate(autobiographyId, requestDto);
    }

    public AutobiographyInitResponseDto coShowInitAutobiography(AutobiographyInitRequestDto requestDto) {
        return autobiographyCommandService.coShowInitAutobiography(requestDto);
    }

    public AutobiographyProgressResponseDto coShowGetAutobiographyProgress(Long autobiographyId) {
        return autobiographyQueryService.coShowGetAutobiographyProgress(autobiographyId);
    }
}
