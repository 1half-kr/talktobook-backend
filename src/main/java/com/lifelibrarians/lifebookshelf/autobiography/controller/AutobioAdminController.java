package com.lifelibrarians.lifebookshelf.autobiography.controller;

import com.lifelibrarians.lifebookshelf.auth.dto.MemberSessionDto;
import com.lifelibrarians.lifebookshelf.auth.jwt.LoginMemberInfo;
import com.lifelibrarians.lifebookshelf.autobiography.dto.request.AutobiographySearchDto;
import com.lifelibrarians.lifebookshelf.common.validate.dto.SortDirection;
import com.lifelibrarians.lifebookshelf.autobiography.dto.response.AutobiographyDetailResponseDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.response.AutobiographyDetailListResponseDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.response.AutobiographyListResponseDto;
import com.lifelibrarians.lifebookshelf.autobiography.service.AutobiographyAdminFacadeService;
import com.lifelibrarians.lifebookshelf.common.validate.dto.request.PaginationDto;
import com.lifelibrarians.lifebookshelf.log.Logging;
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
import javax.validation.constraints.NotNull;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/admin/autobiographies")
@Tag(name = "자서전 - admin (Autobiography-api)", description = "관리자 페이지용 자서전 관련 api")
@Logging
@Validated
public class AutobioAdminController {
    private final AutobiographyAdminFacadeService autobiographyFacadeService;

    @Operation(summary = "관리자의 자서전 검색 및 필터링 조회", 
               description = "관리자 페이지에서 다양한 조건으로 자서전을 검색하고 필터링합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ok"),
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public AutobiographyListResponseDto searchAutobiographies(
            @LoginMemberInfo MemberSessionDto memberSessionDto,
            @Valid @ModelAttribute AutobiographySearchDto searchDto,
            @Valid @ModelAttribute PaginationDto paginationDto
    ) {
        return autobiographyFacadeService.getAllAutobiographiesWithFilters(
                memberSessionDto.getMemberId(),
                searchDto,
                PageRequest.of(paginationDto.getPage(), paginationDto.getSize())
        );
    }

    @Operation(summary = "특정 멤버의 자서전 상세 목록 조회", 
               description = "관리자가 특정 멤버의 모든 자서전 상세 정보를 조회합니다. (통합본 빠른 검색용)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ok"),
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/member/{memberId}/details")
    public AutobiographyDetailListResponseDto getMemberAutobiographyDetails(
            @LoginMemberInfo MemberSessionDto memberSessionDto,
            @Parameter(description = "조회할 멤버 ID", required = true)
            @PathVariable @NotNull Long memberId,
            @Parameter(description = "생성일 정렬 방향", example = "DESC")
            @RequestParam(required = false) SortDirection createdAtSort,
            @Parameter(description = "수정일 정렬 방향", example = "ASC")
            @RequestParam(required = false) SortDirection updatedAtSort,
            @Valid @ModelAttribute PaginationDto paginationDto
    ) {
        // 정렬 옵션을 적용한 PageRequest 생성
        Sort sort = Sort.unsorted();
        
        // createdAt 정렬이 있으면 추가
        if (createdAtSort != null) {
            Sort.Direction direction = createdAtSort == SortDirection.ASC ? 
                    Sort.Direction.ASC : Sort.Direction.DESC;
            sort = sort.and(Sort.by(direction, "createdAt"));
        }
        
        // updatedAt 정렬이 있으면 추가
        if (updatedAtSort != null) {
            Sort.Direction direction = updatedAtSort == SortDirection.ASC ? 
                    Sort.Direction.ASC : Sort.Direction.DESC;
            sort = sort.and(Sort.by(direction, "updatedAt"));
        }
        
        // 정렬 조건이 없으면 기본 정렬 (생성일 내림차순)
        if (sort.isUnsorted()) {
            sort = Sort.by(Sort.Direction.DESC, "createdAt");
        }
        
        PageRequest pageRequest = PageRequest.of(paginationDto.getPage(), paginationDto.getSize(), sort);
        
        return autobiographyFacadeService.getMemberAutobiographyDetails(
                memberSessionDto.getMemberId(),
                memberId,
                pageRequest
        );
    }

    @Operation(summary = "관리자의 특정 자서전 상세 조회", 
               description = "관리자가 특정 자서전의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ok"),
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{autobiographyId}/detail")
    public AutobiographyDetailResponseDto getAutobiographyDetail(
            @LoginMemberInfo MemberSessionDto memberSessionDto,
            @Parameter(description = "조회할 자서전 ID", required = true)
            @PathVariable @NotNull Long autobiographyId
    ) {
        return autobiographyFacadeService.getAutobiographyDetail(
                memberSessionDto.getMemberId(),
                autobiographyId
        );
    }
}
