package com.lifelibrarians.lifebookshelf.member.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "멤버 목록 정보")
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberListResponseDto {

    @ArraySchema(schema = @Schema(implementation = MemberPreviewDto.class))
    private final List<MemberPreviewDto> results;
    
    // 선택 attributes (v1 안정성)
    private final Integer currentPage;
    private final Integer totalPages;
    private final Long totalElements;
    private final Boolean isLast;

    public static MemberListResponseDto fromPage(Page<MemberPreviewDto> page) {
        return MemberListResponseDto.builder()
                .results(page.getContent())
                .currentPage(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .isLast(page.isLast())
                .build();
    }
}
