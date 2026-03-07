package com.lifelibrarians.lifebookshelf.notification.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@Builder
@Getter
@Schema(description = "알림 삭제 처리 요청")
@ToString
@FieldNameConstants
public class NotificationHistoryDeleteRequestDto {

	@Schema(description = "삭제할 알림 ID 목록", example = "[1, 3, 5]")
	private final List<Long> notificationHistoryIds;

	@JsonCreator
	public NotificationHistoryDeleteRequestDto(
			@JsonProperty("notificationHistoryIds") List<Long> notificationHistoryIds) {
		this.notificationHistoryIds = notificationHistoryIds;
	}
}
