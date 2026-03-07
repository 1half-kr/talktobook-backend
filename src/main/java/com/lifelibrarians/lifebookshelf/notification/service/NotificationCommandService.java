package com.lifelibrarians.lifebookshelf.notification.service;

import com.lifelibrarians.lifebookshelf.log.Logging;
import com.lifelibrarians.lifebookshelf.member.domain.Member;
import com.lifelibrarians.lifebookshelf.notification.domain.DeviceRegistry;
import com.lifelibrarians.lifebookshelf.notification.domain.NoticeHistory;
import com.lifelibrarians.lifebookshelf.notification.domain.Notification;
import com.lifelibrarians.lifebookshelf.notification.domain.NotificationSubscribe;
import com.lifelibrarians.lifebookshelf.notification.dto.request.NotificationHistoryDeleteRequestDto;
import com.lifelibrarians.lifebookshelf.notification.dto.request.NotificationHistoryReadRequestDto;
import com.lifelibrarians.lifebookshelf.notification.dto.request.SubscribingNotificationUpdateRequestDto;
import com.lifelibrarians.lifebookshelf.notification.repository.DeviceRegistryRepository;
import com.lifelibrarians.lifebookshelf.notification.repository.NoticeHistoryRepository;
import com.lifelibrarians.lifebookshelf.notification.repository.NotificationSubscribeRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Logging
public class NotificationCommandService {

	private final DeviceRegistryRepository deviceRegistryRepository;
	private final NotificationSubscribeRepository notificationSubscribeRepository;
	private final NoticeHistoryRepository noticeHistoryRepository;

	public void updateDeviceToken(Member member, String deviceToken, LocalDateTime now) {
		List<DeviceRegistry> registries = deviceRegistryRepository
				.findByMemberIdOrderByCreatedAt(member.getId());
		if (registries.stream().anyMatch(r -> r.getToken().equals(deviceToken))) {
			return;
		}
		for (DeviceRegistry registry : registries) {
			if (registry.getExpiredAt().isBefore(now)) {
				deviceRegistryRepository.delete(registry);
			}
		}
		LocalDateTime expiredAt = now.plusDays(DeviceRegistry.DEFAULT_EXPIRED_DAYS);
		DeviceRegistry registry = DeviceRegistry.of(member, deviceToken, now, expiredAt);
		if (registries.size() >= DeviceRegistry.MAX_DEVICE_COUNT) {
			DeviceRegistry oldest = registries.get(0);
			deviceRegistryRepository.delete(oldest);
		}
		deviceRegistryRepository.save(registry);
	}

	public void updateSubscriptions(
			List<NotificationSubscribe> notificationSubscribes,
			List<Notification> notifications,
			SubscribingNotificationUpdateRequestDto requestDto,
			Member member
	) {
		List<Long> newNotificationIds = new ArrayList<>();
		List<Long> removedNotificationIds = new ArrayList<>();

		for (Notification notification : notifications) {
			// 새로 추가할 알림에 추가 (notificationSubscribes에 없음 && requestDto에 있음)
			if (notificationSubscribes.stream()
					.noneMatch(ns -> ns.getNotification().getId().equals(notification.getId()))
					&& requestDto.getNotificationIds().contains(notification.getId())) {
				newNotificationIds.add(notification.getId());
				// 삭제할 알림에 추가 (notificationSubscribes에 존재 && requestDto에 없음)
			} else if (notificationSubscribes.stream()
					.anyMatch(ns -> ns.getNotification().getId().equals(notification.getId()))
					&& !requestDto.getNotificationIds().contains(notification.getId())) {
				removedNotificationIds.add(notification.getId());
			}
		}

		notificationSubscribeRepository.deleteByMemberIdAndNotificationIdIn(member.getId(),
				removedNotificationIds);

		LocalDateTime now = LocalDateTime.now();
		List<NotificationSubscribe> newSubscribes = newNotificationIds.stream()
				.map(notificationId -> NotificationSubscribe.of(now, member,
						notifications.stream()
								.filter(n -> n.getId().equals(notificationId))
								.findFirst()
								.orElseThrow()))
				.collect(Collectors.toList());

		notificationSubscribeRepository.saveAll(newSubscribes);

		updateDeviceToken(member, requestDto.getDeviceToken(), now);
	}

	public void readHistories(Long memberId, NotificationHistoryReadRequestDto requestDto,
			List<NoticeHistory> histories) {
		List<Long> historyIds = requestDto.getNotificationHistoryIds();
		List<Long> targetHistories = histories.stream()
				.map(NoticeHistory::getId)
				.filter(historyIds::contains)
				.collect(Collectors.toList());
		noticeHistoryRepository.updateIsReadByMemberIdAndNotificationIdIn(memberId, targetHistories);
	}

	public void deleteHistories(Long memberId, NotificationHistoryDeleteRequestDto requestDto,
			List<NoticeHistory> histories) {
		List<Long> historyIds = requestDto.getNotificationHistoryIds();
		List<Long> targetHistories = histories.stream()
				.map(NoticeHistory::getId)
				.filter(historyIds::contains)
				.collect(Collectors.toList());
		noticeHistoryRepository.deleteByMemberIdAndIdIn(memberId, targetHistories);
	}
}
