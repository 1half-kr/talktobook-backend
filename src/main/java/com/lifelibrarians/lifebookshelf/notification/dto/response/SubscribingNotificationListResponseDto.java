package com.lifelibrarians.lifebookshelf.notification.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "구독중인 알림 목록 응답")
@ToString
@FieldNameConstants
public class SubscribingNotificationListResponseDto {

	@ArraySchema(schema = @Schema(implementation = SubscribingNotificationDto.class))
	private final List<SubscribingNotificationDto> results;
}
