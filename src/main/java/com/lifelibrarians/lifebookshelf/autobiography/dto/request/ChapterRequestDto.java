package com.lifelibrarians.lifebookshelf.autobiography.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import java.util.List;
import lombok.experimental.FieldNameConstants;

@Builder
@Getter
@Schema(description = "자서전 챕터 정보")
@ToString
@FieldNameConstants
public class ChapterRequestDto {

	@Schema(description = "챕터 번호", example = "1")
	private final String number;

	@Schema(description = "챕터 이름", example = "나의 첫번째 챕터")
	private final String name;

	@Schema(description = "챕터 설명", example = "나의 첫번째 챕터에 대한 설명")
	private final String description;

	@ArraySchema(schema = @Schema(implementation = SubchapterRequestDto.class))
	private final List<SubchapterRequestDto> subchapters;

	@JsonCreator
	public ChapterRequestDto(@JsonProperty("number") String number,
			@JsonProperty("name") String name,
			@JsonProperty("description") String description,
			@JsonProperty("subchapters") List<SubchapterRequestDto> subchapters) {
		this.number = number;
		this.name = name;
		this.description = description;
		this.subchapters = subchapters;
	}
}