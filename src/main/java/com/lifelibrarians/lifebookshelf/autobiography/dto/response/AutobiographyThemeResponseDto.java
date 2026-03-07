package com.lifelibrarians.lifebookshelf.autobiography.dto.response;

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
@Schema(description = "자서전 테마 응답 DTO")
@ToString
public class AutobiographyThemeResponseDto {
    @Schema(description = "테마 이름", example = "Family")
    private final String name;

    @Schema(description = "테마 order 리스트 조회", example = "[1, 2, 3]")
    private final List<Integer> categories;
}
