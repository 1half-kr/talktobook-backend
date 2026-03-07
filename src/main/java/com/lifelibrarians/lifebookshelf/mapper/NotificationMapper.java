package com.lifelibrarians.lifebookshelf.mapper;

import com.lifelibrarians.lifebookshelf.notification.domain.NoticeHistory;
import com.lifelibrarians.lifebookshelf.notification.domain.Notification;
import com.lifelibrarians.lifebookshelf.notification.dto.response.NotificationHistoryDto;
import com.lifelibrarians.lifebookshelf.notification.dto.response.NotificationHistoryListResponseDto;
import com.lifelibrarians.lifebookshelf.notification.dto.response.SubscribingNotificationDto;
import java.time.LocalDateTime;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

	@Mapping(source = "notification.id", target = "notificationId")
	SubscribingNotificationDto toSubscribingNotificationDto(Notification notification,
			LocalDateTime subscribedAt);

	@Mapping(source = "noticeHistory.id", target = "noticeHistoryId")
	NotificationHistoryDto toNotificationHistoryDto(NoticeHistory noticeHistory);

	@Mapping(source = "noticeHistories", target = "results")
	NotificationHistoryListResponseDto toNotificationHistoryListResponseDto(
			List<NotificationHistoryDto> noticeHistories,
			int currentPage,
			int totalElements,
			int totalPages,
			boolean hasNextPage,
			boolean hasPreviousPage);
}
