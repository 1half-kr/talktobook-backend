package com.lifelibrarians.lifebookshelf.app.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "모든 app 버전 정보 응답 DTO")
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReadAllAppVersionsResponseDto {

    @Schema(description = "전체 앱 버전 목록")
    List<ReadAppVersionResponseDto> appVersions;
}
