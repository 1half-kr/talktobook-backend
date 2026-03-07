package com.lifelibrarians.lifebookshelf.notification.dto.response;

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
@Schema(description = "알림 내역 DTO")
@ToString
@FieldNameConstants
public class NotificationHistoryDto {

	@Schema(description = "알림 내역 ID", example = "1")
	private final Long noticeHistoryId;

	@Schema(description = "제목", example = "[공지 알림] 인생책방 시스템 점검 안내")
	private final String title;

	@Schema(description = "내용", example = "인생책방 시스템이 금일 자정부터 약 4시간 점검 예정입니다.")
	private final String content;

	@Schema(description = "수신 일시", example = "2023-01-01T00:00:00Z")
	private final LocalDateTime receivedAt;

	@Schema(description = "읽음 여부", example = "false")
	private final Boolean isRead;
}
