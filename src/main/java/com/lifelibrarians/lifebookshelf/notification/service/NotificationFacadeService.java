package com.lifelibrarians.lifebookshelf.notification.service;

import com.lifelibrarians.lifebookshelf.log.Logging;
import com.lifelibrarians.lifebookshelf.member.domain.Member;
import com.lifelibrarians.lifebookshelf.notification.domain.NoticeHistory;
import com.lifelibrarians.lifebookshelf.notification.domain.Notification;
import com.lifelibrarians.lifebookshelf.notification.domain.NotificationSubscribe;
import com.lifelibrarians.lifebookshelf.notification.dto.request.NotificationHistoryDeleteRequestDto;
import com.lifelibrarians.lifebookshelf.notification.dto.request.NotificationHistoryReadRequestDto;
import com.lifelibrarians.lifebookshelf.notification.dto.request.SubscribingNotificationUpdateRequestDto;
import com.lifelibrarians.lifebookshelf.notification.dto.response.NotificationHistoryListResponseDto;
import com.lifelibrarians.lifebookshelf.notification.dto.response.SubscribingNotificationListResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Logging
public class NotificationFacadeService {

	private final NotificationCommandService notificationCommandService;
	private final NotificationQueryService notificationQueryService;

	public SubscribingNotificationListResponseDto getSubscriptions(Long memberId) {
		return notificationQueryService.getSubscriptions(memberId);
	}

	public SubscribingNotificationListResponseDto updateSubscriptions(Long memberId,
			SubscribingNotificationUpdateRequestDto requestDto) {
		Member member = notificationQueryService.getMember(memberId);
		List<NotificationSubscribe> notificationSubscribes = notificationQueryService
				.getNotificationSubscribes(memberId);
		List<Notification> notifications = notificationQueryService.getNotifications();
		notificationCommandService.updateSubscriptions(notificationSubscribes, notifications,
				requestDto, member);
		return getSubscriptions(memberId);
	}

	public NotificationHistoryListResponseDto getHistories(Long memberId, Pageable pageable) {
		return notificationQueryService.getNotificationHistories(memberId, pageable);
	}

	public void readHistories(Long memberId,
			NotificationHistoryReadRequestDto requestDto) {
		List<NoticeHistory> histories = notificationQueryService.getHistories(memberId);
		notificationCommandService.readHistories(memberId, requestDto, histories);
	}

	public void deleteHistories(Long memberId, NotificationHistoryDeleteRequestDto requestDto) {
		List<NoticeHistory> histories = notificationQueryService.getHistories(memberId);
		notificationCommandService.deleteHistories(memberId, requestDto, histories);
	}
}
