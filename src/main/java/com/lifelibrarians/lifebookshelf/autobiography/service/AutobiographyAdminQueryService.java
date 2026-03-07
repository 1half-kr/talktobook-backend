package com.lifelibrarians.lifebookshelf.autobiography.service;

import com.lifelibrarians.lifebookshelf.autobiography.domain.Autobiography;
import com.lifelibrarians.lifebookshelf.autobiography.dto.request.AutobiographySearchDto;
import com.lifelibrarians.lifebookshelf.common.validate.dto.SortDirection;
import com.lifelibrarians.lifebookshelf.autobiography.dto.response.AutobiographyDetailResponseDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.response.AutobiographyDetailListResponseDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.response.AutobiographyDetailResponseDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.response.AutobiographyListResponseDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.response.AutobiographyPreviewDto;
import com.lifelibrarians.lifebookshelf.autobiography.repository.AutobiographyRepository;
import com.lifelibrarians.lifebookshelf.exception.status.AuthExceptionStatus;
import com.lifelibrarians.lifebookshelf.exception.status.AutobiographyExceptionStatus;
import com.lifelibrarians.lifebookshelf.log.Logging;
import com.lifelibrarians.lifebookshelf.mapper.AutobiographyMapper;
import com.lifelibrarians.lifebookshelf.member.domain.Member;
import com.lifelibrarians.lifebookshelf.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Logging
public class AutobiographyAdminQueryService {

    private final AutobiographyRepository autobiographyRepository;
    private final MemberRepository memberRepository;
    private final AutobiographyMapper autobiographyMapper;

    // 해당 유저가 관리자인지 확인하는 service method
    public boolean isAdminMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(AuthExceptionStatus.MEMBER_NOT_FOUND::toServiceException);

        return member.getRole() != null && member.isAdmin();
    }

    // 관리자 권한으로 검색 및 필터링된 자서전 조회
    public AutobiographyListResponseDto getAllAutobiographiesWithFilters(
            AutobiographySearchDto searchDto, PageRequest pageRequest) {
        
        // 기본값 처리
        String search = searchDto.hasSearchFilter() ? searchDto.getSearch() : "";
        Integer memberId = searchDto.hasMemberFilter() ? searchDto.getMemberId() : 0;
        
        // 정렬 옵션 적용
        PageRequest sortedPageRequest = applySorting(pageRequest, searchDto);
        
        Page<Autobiography> autobiographies = autobiographyRepository.findAllWithFilters(
                search,
                searchDto.getHasCoverImage(),
                memberId,
                searchDto.getCreatedAtStart(),
                searchDto.getCreatedAtEnd(),
                searchDto.getUpdatedAtStart(),
                searchDto.getUpdatedAtEnd(),
                sortedPageRequest
        );

        Page<AutobiographyPreviewDto> autobiographyPreviewDtos =
                autobiographies.map(a ->
                        autobiographyMapper.toAutobiographyPreviewDto(
                                a,
                                a.getAutobiographyStatus() // fetch join 덕분에 이미 로딩됨
                        )
                );

        return AutobiographyListResponseDto.fromPage(autobiographyPreviewDtos);
    }

    // 특정 멤버의 자서전 상세 목록 조회 (통합본 빠른 검색용)
    public AutobiographyDetailListResponseDto getMemberAutobiographyDetails(
            Long memberId, PageRequest pageRequest) {
        
        // 기본 정렬: 생성일 내림차순
        Sort defaultSort = Sort.by(Sort.Direction.DESC, "createdAt");
        PageRequest sortedPageRequest = PageRequest.of(
                pageRequest.getPageNumber(), 
                pageRequest.getPageSize(), 
                defaultSort
        );
        
        Page<Autobiography> autobiographies = autobiographyRepository.findDetailsByMemberId(memberId, sortedPageRequest);
        
        Page<AutobiographyDetailResponseDto> autobiographyDetailDtos = autobiographies.map(autobiography -> 
                autobiographyMapper.toAutobiographyDetailResponseDto(autobiography));

        return AutobiographyDetailListResponseDto.fromPage(autobiographyDetailDtos);
    }

    public AutobiographyDetailResponseDto getAutobiographyDetail(Long autobiographyId) {
        Autobiography autobiography = autobiographyRepository.findById(autobiographyId)
                .orElseThrow(AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND::toServiceException);
        
        return autobiographyMapper.toAutobiographyDetailResponseDto(autobiography);
    }

    // 별개의 정렬 옵션을 PageRequest에 적용하는 헬퍼 메서드
    private PageRequest applySorting(PageRequest pageRequest, AutobiographySearchDto searchDto) {
        Sort sort = Sort.unsorted();
        
        // createdAt 정렬이 있으면 추가
        if (searchDto.hasCreatedAtSort()) {
            Sort.Direction direction = searchDto.getCreatedAtSort() == SortDirection.ASC ? 
                    Sort.Direction.ASC : Sort.Direction.DESC;
            sort = sort.and(Sort.by(direction, "createdAt"));
        }
        
        // updatedAt 정렬이 있으면 추가
        if (searchDto.hasUpdatedAtSort()) {
            Sort.Direction direction = searchDto.getUpdatedAtSort() == SortDirection.ASC ? 
                    Sort.Direction.ASC : Sort.Direction.DESC;
            sort = sort.and(Sort.by(direction, "updatedAt"));
        }
        
        // 정렬 조건이 없으면 기본 정렬 (생성일 내림차순)
        if (sort.isUnsorted()) {
            sort = Sort.by(Sort.Direction.DESC, "createdAt");
        }
        
        return PageRequest.of(pageRequest.getPageNumber(), pageRequest.getPageSize(), sort);
    }
}
