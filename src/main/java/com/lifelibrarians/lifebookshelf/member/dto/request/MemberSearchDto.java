package com.lifelibrarians.lifebookshelf.member.dto.request;

import com.lifelibrarians.lifebookshelf.common.validate.dto.SortDirection;
import com.lifelibrarians.lifebookshelf.member.domain.LoginType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "관리자용 멤버 검색 및 필터링 DTO")
@ToString
public class MemberSearchDto {

    @Schema(description = "이메일 키워드 검색 (빈 문자열이면 전체 검색)", example = "user@example.com")
    @Builder.Default
    private final String emailSearch = "";

    @Schema(description = "닉네임 키워드 검색 (빈 문자열이면 전체 검색)", example = "홍길동")
    @Builder.Default
    private final String nicknameSearch = "";

    @Schema(description = "이름 키워드 검색 (빈 문자열이면 전체 검색)", example = "홍길동")
    @Builder.Default
    private final String nameSearch = "";

    @Schema(description = "로그인 타입 필터 (null이면 전체)", example = "SOCIAL")
    private final LoginType loginType;

    @Schema(description = "프로필 이미지 존재 여부 필터 (true: 있음, false: 없음, null: 전체)", example = "true")
    private final Boolean hasProfileImage;

    @Schema(description = "가입일 시작 범위", example = "2023-01-01T00:00:00")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private final LocalDateTime createdAtStart;

    @Schema(description = "가입일 종료 범위", example = "2023-12-31T23:59:59")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private final LocalDateTime createdAtEnd;

    @Schema(description = "가입일 기준 정렬 방향 (null이면 가입일 정렬 안함)", example = "DESC")
    private final SortDirection createdAtSort;

    // 필터링 조건이 적용되는지 확인하는 헬퍼 메서드들
    public boolean hasEmailSearch() {
        return emailSearch != null && !emailSearch.trim().isEmpty();
    }

    public boolean hasNicknameSearch() {
        return nicknameSearch != null && !nicknameSearch.trim().isEmpty();
    }

    public boolean hasNameSearch() {
        return nameSearch != null && !nameSearch.trim().isEmpty();
    }

    public boolean hasCreatedAtFilter() {
        return createdAtStart != null || createdAtEnd != null;
    }

    public boolean hasCreatedAtSort() {
        return createdAtSort != null;
    }
}
