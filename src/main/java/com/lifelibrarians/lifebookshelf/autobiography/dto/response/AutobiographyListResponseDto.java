package com.lifelibrarians.lifebookshelf.autobiography.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Page;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "자서전 목록 정보")
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AutobiographyListResponseDto {

	@ArraySchema(schema = @Schema(implementation = AutobiographyPreviewDto.class))
	private final List<AutobiographyPreviewDto> results;
	// 선택 attributes (v1 안정성)
	private final Integer currentPage;
	private final Integer totalPages;
	private final Long totalElements;
	private final Boolean isLast;

	public static AutobiographyListResponseDto fromPage(Page<AutobiographyPreviewDto> page) {
		return AutobiographyListResponseDto.builder()
				.results(page.getContent())
				.currentPage(page.getNumber())
				.totalPages(page.getTotalPages())
				.totalElements(page.getTotalElements())
				.isLast(page.isLast())
				.build();
	}
}
