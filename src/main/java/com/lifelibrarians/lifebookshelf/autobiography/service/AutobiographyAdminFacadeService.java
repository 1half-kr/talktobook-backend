package com.lifelibrarians.lifebookshelf.autobiography.service;

import com.lifelibrarians.lifebookshelf.autobiography.dto.request.AutobiographySearchDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.response.AutobiographyDetailResponseDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.response.AutobiographyDetailListResponseDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.response.AutobiographyListResponseDto;
import com.lifelibrarians.lifebookshelf.exception.status.AuthExceptionStatus;
import com.lifelibrarians.lifebookshelf.log.Logging;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Logging
public class AutobiographyAdminFacadeService {
    private final AutobiographyAdminQueryService autobiographyQueryService;
    
    /** READ **/
    public AutobiographyListResponseDto getAllAutobiographiesWithFilters(
            Long memberId, AutobiographySearchDto searchDto, PageRequest pageRequest) {
        // admin인지 확인
        if (!autobiographyQueryService.isAdminMember(memberId)) {
            throw AuthExceptionStatus.MEMBER_IS_NOT_ADMIN.toServiceException();
        }

        return autobiographyQueryService.getAllAutobiographiesWithFilters(searchDto, pageRequest);
    }

    public AutobiographyDetailListResponseDto getMemberAutobiographyDetails(
            Long adminMemberId, Long targetMemberId, PageRequest pageRequest) {
        // admin인지 확인
        if (!autobiographyQueryService.isAdminMember(adminMemberId)) {
            throw AuthExceptionStatus.MEMBER_IS_NOT_ADMIN.toServiceException();
        }

        return autobiographyQueryService.getMemberAutobiographyDetails(targetMemberId, pageRequest);
    }

    public AutobiographyDetailResponseDto getAutobiographyDetail(Long adminMemberId, Long autobiographyId) {
        // admin인지 확인
        if (!autobiographyQueryService.isAdminMember(adminMemberId)) {
            throw AuthExceptionStatus.MEMBER_IS_NOT_ADMIN.toServiceException();
        }

        return autobiographyQueryService.getAutobiographyDetail(autobiographyId);
    }
}
