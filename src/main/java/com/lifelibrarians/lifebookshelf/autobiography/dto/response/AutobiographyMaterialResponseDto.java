package com.lifelibrarians.lifebookshelf.autobiography.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "자서전에 등록된 소재 정보")
@ToString
public class AutobiographyMaterialResponseDto {
    @Schema(description = "material ID", example = "1")
    private final Long id;

    @Schema(description = "material 구분 순서", example = "1")
    private final Integer order;

    @Schema(description = "materials 순위", example = "1")
    @Setter
    private Integer rank;

    @Schema(description = "materials 이름", example = "출생지(고향)")
    private final String name;

    @Schema(description = "이미지 URL", example = "https://example.com/bio-cover-images/random-string/image1.jpg")
    private final String imageUrl;

    @Schema(description = "소재 개수", example = "5")
    private final Integer count;
}
