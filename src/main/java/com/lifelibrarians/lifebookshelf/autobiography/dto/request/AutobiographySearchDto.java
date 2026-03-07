package com.lifelibrarians.lifebookshelf.autobiography.dto.request;

import com.lifelibrarians.lifebookshelf.autobiography.validate.AutobiographySearchValidation;
import com.lifelibrarians.lifebookshelf.common.validate.dto.SortDirection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "관리자용 자서전 필터링 DTO")
@ToString
@FieldNameConstants
@AutobiographySearchValidation
public class AutobiographySearchDto {

    @Schema(description = "제목 키워드 검색 (빈 문자열이면 전체 검색)", example = "My Life")
    @Builder.Default
    private final String search = "";

    @Schema(description = "커버 이미지 존재 여부 필터 (true: 있음, false: 없음, null: 전체)", example = "true")
    private final Boolean hasCoverImage;

    @Schema(description = "특정 멤버 ID로 필터링 (0이면 전체)", example = "1")
    @Builder.Default
    private final Integer memberId = 0;

    @Schema(description = "생성일 시작 범위", example = "2023-01-01T00:00:00")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private final LocalDateTime createdAtStart;

    @Schema(description = "생성일 종료 범위", example = "2023-12-31T23:59:59")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private final LocalDateTime createdAtEnd;

    @Schema(description = "수정일 시작 범위", example = "2023-01-01T00:00:00")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private final LocalDateTime updatedAtStart;

    @Schema(description = "수정일 종료 범위", example = "2023-12-31T23:59:59")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private final LocalDateTime updatedAtEnd;

    @Schema(description = "생성일 기준 정렬 방향 (null이면 생성일 정렬 안함)", example = "DESC")
    private final SortDirection createdAtSort;

    @Schema(description = "수정일 기준 정렬 방향 (null이면 수정일 정렬 안함)", example = "ASC")
    private final SortDirection updatedAtSort;

    // 필터링 조건이 적용되는지 확인하는 헬퍼 메서드들
    public boolean hasSearchFilter() {
        return search != null && !search.trim().isEmpty();
    }

    public boolean hasMemberFilter() {
        return memberId != null && memberId > 0;
    }

    public boolean hasCreatedAtFilter() {
        return createdAtStart != null || createdAtEnd != null;
    }

    public boolean hasUpdatedAtFilter() {
        return updatedAtStart != null || updatedAtEnd != null;
    }

    public boolean hasCreatedAtSort() {
        return createdAtSort != null;
    }

    public boolean hasUpdatedAtSort() {
        return updatedAtSort != null;
    }
}
