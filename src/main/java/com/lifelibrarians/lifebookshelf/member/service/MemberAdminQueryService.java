package com.lifelibrarians.lifebookshelf.member.service;

import com.lifelibrarians.lifebookshelf.common.validate.dto.SortDirection;
import com.lifelibrarians.lifebookshelf.exception.status.AuthExceptionStatus;
import com.lifelibrarians.lifebookshelf.interview.repository.InterviewRepository;
import com.lifelibrarians.lifebookshelf.log.Logging;
import com.lifelibrarians.lifebookshelf.mapper.MemberMapper;
import com.lifelibrarians.lifebookshelf.member.domain.Member;
import com.lifelibrarians.lifebookshelf.member.domain.MemberRole;
import com.lifelibrarians.lifebookshelf.member.dto.request.MemberSearchDto;
import com.lifelibrarians.lifebookshelf.member.dto.response.MemberDetailDto;
import com.lifelibrarians.lifebookshelf.member.dto.response.MemberDetailListResponseDto;
import com.lifelibrarians.lifebookshelf.member.dto.response.MemberListResponseDto;
import com.lifelibrarians.lifebookshelf.member.dto.response.MemberPreviewDto;
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
public class MemberAdminQueryService {

    private final MemberRepository memberRepository;
    private final InterviewRepository interviewRepository;
    private final MemberMapper memberMapper;

    // 해당 유저가 관리자인지 확인하는 service method
    public boolean isAdminMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(AuthExceptionStatus.MEMBER_NOT_FOUND::toServiceException);

        return member.getRole() != null && member.isAdmin();
    }

    // 관리자 권한으로 검색 및 필터링된 멤버 조회
    public MemberListResponseDto getAllMembersWithFilters(
            MemberSearchDto searchDto, PageRequest pageRequest) {
        
        // 기본값 처리 - null 체크 추가
        String emailSearch = (searchDto.getEmailSearch() != null && !searchDto.getEmailSearch().trim().isEmpty()) 
                ? searchDto.getEmailSearch().trim() : "";
        String nicknameSearch = (searchDto.getNicknameSearch() != null && !searchDto.getNicknameSearch().trim().isEmpty()) 
                ? searchDto.getNicknameSearch().trim() : "";
        String nameSearch = (searchDto.getNameSearch() != null && !searchDto.getNameSearch().trim().isEmpty()) 
                ? searchDto.getNameSearch().trim() : "";
        
        // 정렬 옵션 적용
        PageRequest sortedPageRequest = applySorting(pageRequest, searchDto);
        
        Page<Member> members = memberRepository.findAllWithFilters(
                emailSearch,
                nicknameSearch,
                nameSearch,
                searchDto.getLoginType(),
                searchDto.getHasProfileImage(),
                searchDto.getCreatedAtStart(),
                searchDto.getCreatedAtEnd(),
                sortedPageRequest
        );

        Page<MemberPreviewDto> memberPreviewDtos = members.map(this::toMemberPreviewDto);

        return MemberListResponseDto.fromPage(memberPreviewDtos);
    }

    // 특정 역할의 멤버 상세 목록 조회
    public MemberDetailListResponseDto getMemberDetailsByRole(
            MemberRole role, PageRequest pageRequest) {
        
        // 기본 정렬: 가입일 내림차순
        Sort defaultSort = Sort.by(Sort.Direction.DESC, "createdAt");
        PageRequest sortedPageRequest = PageRequest.of(
                pageRequest.getPageNumber(), 
                pageRequest.getPageSize(), 
                defaultSort
        );
        
        Page<Member> members = memberRepository.findDetailsByRole(role, sortedPageRequest);
        
        Page<MemberDetailDto> memberDetailDtos = members.map(memberMapper::toMemberDetailDto);

        return MemberDetailListResponseDto.fromPage(memberDetailDtos);
    }

    // 정렬 옵션을 PageRequest에 적용하는 헬퍼 메서드
    private PageRequest applySorting(PageRequest pageRequest, MemberSearchDto searchDto) {
        Sort sort = Sort.unsorted();
        
        // createdAt 정렬이 있으면 추가
        if (searchDto.hasCreatedAtSort()) {
            Sort.Direction direction = searchDto.getCreatedAtSort() == SortDirection.ASC ? 
                    Sort.Direction.ASC : Sort.Direction.DESC;
            sort = sort.and(Sort.by(direction, "createdAt"));
        }
        
        // 정렬 조건이 없으면 기본 정렬 (가입일 내림차순)
        if (sort.isUnsorted()) {
            sort = Sort.by(Sort.Direction.DESC, "createdAt");
        }
        
        return PageRequest.of(pageRequest.getPageNumber(), pageRequest.getPageSize(), sort);
    }

    // Member를 MemberPreviewDto로 변환하는 헬퍼 메서드
    private MemberPreviewDto toMemberPreviewDto(Member member) {
        // 실제 인터뷰 횟수 조회
        Long interviewCountLong = interviewRepository.countByMemberId(member.getId());
        int interviewCount = interviewCountLong != null ? interviewCountLong.intValue() : 0;
        
        // 자서전 개수 계산
        int autobiographyCount = member.getMemberAutobiographies() != null ? member.getMemberAutobiographies().size() : 0;
        
        // 멤버 메타데이터에서 정보 추출
        MemberPreviewDto.MemberMetadata metadata = null;
        if (member.getMemberMemberMetadata() != null) {
            var memberMetadata = member.getMemberMemberMetadata();
            metadata = MemberPreviewDto.MemberMetadata.builder()
                    .name(memberMetadata.getName())
                    .gender(memberMetadata.getGender() != null ? memberMetadata.getGender().toString() : null)
                    .educationLevel(memberMetadata.getEducationLevel())
                    .maritalStatus(memberMetadata.getMaritalStatus())
                    .build();
        }
        
        // 활동 요약 정보
        MemberPreviewDto.ActivitySummary activitySummary = MemberPreviewDto.ActivitySummary.builder()
                .interviewCount(interviewCount)
                .autobiographyCount(autobiographyCount)
                .build();
        
        return MemberPreviewDto.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .loginType(member.getLoginType())
                .profileImageUrl(member.getProfileImageUrl())
                .createdAt(member.getCreatedAt())
                .metadata(metadata)
                .activitySummary(activitySummary)
                .build();
    }
}
