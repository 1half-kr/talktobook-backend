package com.lifelibrarians.lifebookshelf.common.validate.dto.request;

import com.lifelibrarians.lifebookshelf.common.validate.PaginationValidation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@Getter
@Builder
@Schema(description = "page, size 페이징 정보")
@ToString
@FieldNameConstants
@PaginationValidation
public class PaginationDto {

    @Schema(description = "page", example = "0")
    private final Integer page;
    @Schema(description = "size", example = "10")
    private final Integer size;
}