package com.lifelibrarians.lifebookshelf.autobiography.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@Builder
@Getter
@Schema(description = "자서전 서브챕터 정보")
@ToString
@FieldNameConstants
public class SubchapterRequestDto {

	@Schema(description = "서브챕터 번호", example = "1.1")
	private final String number;

	@Schema(description = "서브챕터 이름", example = "나의 첫번째 서브챕터")
	private final String name;

	@Schema(description = "챕터 설명", example = "나의 첫번째 서브챕터에 대한 설명")
	private final String description;

	@JsonCreator
	public SubchapterRequestDto(
			@JsonProperty("number") String number,
			@JsonProperty("name") String name,
			@JsonProperty("description") String description) {
		this.number = number;
		this.name = name;
		this.description = description;
	}
}
