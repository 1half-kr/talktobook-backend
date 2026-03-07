package com.lifelibrarians.lifebookshelf.notification.dto.response;

import com.lifelibrarians.lifebookshelf.notification.domain.NoticeType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "구독중인 알림 DTO")
@ToString
@FieldNameConstants
public class SubscribingNotificationDto {

	@Schema(description = "알림 ID", example = "1")
	private final Long notificationId;

	@Schema(description = "알림 유형", example = "ANNOUNCEMENT")
	private final NoticeType noticeType;

	@Schema(description = "알림 설명", example = "시스템에서 제공하는 공지 알림")
	private final String description;

	@Schema(description = "구독 일시", example = "2023-01-01T00:00:00Z")
	private final LocalDateTime subscribedAt;
}
