package com.lifelibrarians.lifebookshelf.autobiography.dto.response;

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
@Schema(description = "자서전 상세 목록 정보")
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AutobiographyDetailListResponseDto {

    @ArraySchema(schema = @Schema(implementation = AutobiographyDetailResponseDto.class))
    private final List<AutobiographyDetailResponseDto> results;
    
    // 선택 attributes (v1 안정성)
    private final Integer currentPage;
    private final Integer totalPages;
    private final Long totalElements;
    private final Boolean isLast;

    public static AutobiographyDetailListResponseDto fromPage(Page<AutobiographyDetailResponseDto> page) {
        return AutobiographyDetailListResponseDto.builder()
                .results(page.getContent())
                .currentPage(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .isLast(page.isLast())
                .build();
    }
}
