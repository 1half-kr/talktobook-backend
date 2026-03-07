package com.lifelibrarians.lifebookshelf.member.service;

import com.lifelibrarians.lifebookshelf.exception.status.AuthExceptionStatus;
import com.lifelibrarians.lifebookshelf.log.Logging;
import com.lifelibrarians.lifebookshelf.member.domain.MemberRole;
import com.lifelibrarians.lifebookshelf.member.dto.request.MemberSearchDto;
import com.lifelibrarians.lifebookshelf.member.dto.response.MemberDetailListResponseDto;
import com.lifelibrarians.lifebookshelf.member.dto.response.MemberListResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Logging
public class MemberAdminFacadeService {
    private final MemberAdminQueryService memberQueryService;
    
    /** READ **/
    public MemberListResponseDto getAllMembersWithFilters(
            Long memberId, MemberSearchDto searchDto, PageRequest pageRequest) {
        // admin인지 확인
        if (!memberQueryService.isAdminMember(memberId)) {
            throw AuthExceptionStatus.MEMBER_IS_NOT_ADMIN.toServiceException();
        }

        return memberQueryService.getAllMembersWithFilters(searchDto, pageRequest);
    }

    public MemberDetailListResponseDto getMemberDetailsByRole(
            Long adminMemberId, MemberRole role, PageRequest pageRequest) {
        // admin인지 확인
        if (!memberQueryService.isAdminMember(adminMemberId)) {
            throw AuthExceptionStatus.MEMBER_IS_NOT_ADMIN.toServiceException();
        }

        return memberQueryService.getMemberDetailsByRole(role, pageRequest);
    }
}
