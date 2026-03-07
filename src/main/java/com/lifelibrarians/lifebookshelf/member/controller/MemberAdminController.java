package com.lifelibrarians.lifebookshelf.member.controller;

import com.lifelibrarians.lifebookshelf.auth.dto.MemberSessionDto;
import com.lifelibrarians.lifebookshelf.auth.jwt.LoginMemberInfo;
import com.lifelibrarians.lifebookshelf.common.validate.dto.SortDirection;
import com.lifelibrarians.lifebookshelf.common.validate.dto.request.PaginationDto;
import com.lifelibrarians.lifebookshelf.log.Logging;
import com.lifelibrarians.lifebookshelf.member.domain.MemberRole;
import com.lifelibrarians.lifebookshelf.member.dto.request.MemberSearchDto;
import com.lifelibrarians.lifebookshelf.member.dto.response.MemberDetailListResponseDto;
import com.lifelibrarians.lifebookshelf.member.dto.response.MemberListResponseDto;
import com.lifelibrarians.lifebookshelf.member.service.MemberAdminFacadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/admin/members")
@Tag(name = "멤버 - admin (member-api)", description = "관리자 페이지용 회원 관련 API")
@Logging
@Validated
public class MemberAdminController {
    private final MemberAdminFacadeService memberFacadeService;

    @Operation(summary = "관리자의 멤버 검색 및 필터링 조회", 
               description = "관리자 페이지에서 다양한 조건으로 멤버를 검색하고 필터링합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ok"),
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public MemberListResponseDto searchMembers(
            @LoginMemberInfo MemberSessionDto memberSessionDto,
            @Valid @ModelAttribute MemberSearchDto searchDto,
            @Valid @ModelAttribute PaginationDto paginationDto
    ) {
        return memberFacadeService.getAllMembersWithFilters(
                memberSessionDto.getMemberId(),
                searchDto,
                PageRequest.of(paginationDto.getPage(), paginationDto.getSize())
        );
    }
}
