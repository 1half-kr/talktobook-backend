package com.lifelibrarians.lifebookshelf.notification.service;

import com.lifelibrarians.lifebookshelf.exception.status.AuthExceptionStatus;
import com.lifelibrarians.lifebookshelf.log.Logging;
import com.lifelibrarians.lifebookshelf.mapper.NotificationMapper;
import com.lifelibrarians.lifebookshelf.member.domain.Member;
import com.lifelibrarians.lifebookshelf.member.repository.MemberRepository;
import com.lifelibrarians.lifebookshelf.notification.domain.NoticeHistory;
import com.lifelibrarians.lifebookshelf.notification.domain.Notification;
import com.lifelibrarians.lifebookshelf.notification.domain.NotificationSubscribe;
import com.lifelibrarians.lifebookshelf.notification.dto.response.NotificationHistoryDto;
import com.lifelibrarians.lifebookshelf.notification.dto.response.NotificationHistoryListResponseDto;
import com.lifelibrarians.lifebookshelf.notification.dto.response.SubscribingNotificationDto;
import com.lifelibrarians.lifebookshelf.notification.dto.response.SubscribingNotificationListResponseDto;
import com.lifelibrarians.lifebookshelf.notification.repository.NoticeHistoryRepository;
import com.lifelibrarians.lifebookshelf.notification.repository.NotificationRepository;
import com.lifelibrarians.lifebookshelf.notification.repository.NotificationSubscribeRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Logging
public class NotificationQueryService {

	private final MemberRepository memberRepository;
	private final NotificationMapper notificationMapper;
	private final NotificationSubscribeRepository notificationSubscribeRepository;
	private final NotificationRepository notificationRepository;
	private final NoticeHistoryRepository noticeHistoryRepository;

	public Member getMember(Long memberId) {
		return memberRepository.findById(memberId)
				.orElseThrow(AuthExceptionStatus.MEMBER_NOT_FOUND::toServiceException);
	}

	public List<Notification> getNotifications() {
		return notificationRepository.findAll();
	}

	public List<NotificationSubscribe> getNotificationSubscribes(Long memberId) {
		return notificationSubscribeRepository.findByMemberId(memberId);
	}

	public List<NoticeHistory> getHistories(Long memberId) {
		return noticeHistoryRepository.findByMemberId(memberId);
	}

	public SubscribingNotificationListResponseDto getSubscriptions(Long memberId) {
		List<NotificationSubscribe> notificationSubscribes = getNotificationSubscribes(memberId);
		List<Notification> notifications = getNotifications();
		List<SubscribingNotificationDto> subscribingNotificationDtos = notifications.stream()
				.map(notification -> {
					for (NotificationSubscribe notificationSubscribe : notificationSubscribes) {
						if (notificationSubscribe.getNotification().getId().equals(notification.getId())) {
							return notificationMapper.toSubscribingNotificationDto(notification,
									notificationSubscribe.getSubscribedAt());
						}
					}
					return notificationMapper.toSubscribingNotificationDto(notification, null);
				}).collect(Collectors.toList());
		return SubscribingNotificationListResponseDto.builder()
				.results(subscribingNotificationDtos)
				.build();
	}

	public NotificationHistoryListResponseDto getNotificationHistories(Long memberId,
			Pageable pageable) {
		Page<NoticeHistory> noticeHistories = noticeHistoryRepository.findByMemberId(memberId,
				pageable);
		List<NotificationHistoryDto> notificationHistoryDtos = noticeHistories.stream()
				.map(notificationMapper::toNotificationHistoryDto)
				.collect(Collectors.toList());

		return notificationMapper.toNotificationHistoryListResponseDto(
				notificationHistoryDtos,
				pageable.getPageNumber(),
				(int) noticeHistories.getTotalElements(),
				noticeHistories.getTotalPages(),
				noticeHistories.hasNext(),
				noticeHistories.hasPrevious()
		);
	}
}
