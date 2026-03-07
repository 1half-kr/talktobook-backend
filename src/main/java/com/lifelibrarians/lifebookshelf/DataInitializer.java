package com.lifelibrarians.lifebookshelf;

import com.lifelibrarians.lifebookshelf.notification.domain.NoticeType;
import com.lifelibrarians.lifebookshelf.notification.domain.Notification;
import com.lifelibrarians.lifebookshelf.notification.repository.NotificationRepository;
import java.time.LocalDateTime;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer {

	private final NotificationRepository notificationRepository;

	@PostConstruct
	public void initData() {
		List<Notification> notifications = notificationRepository.findAll();
		if (notifications.isEmpty()) {
			notificationRepository.saveAll(List.of(
					Notification.of(NoticeType.ANNOUNCEMENT, "공지사항 알림", LocalDateTime.now()),
					Notification.of(NoticeType.INTERVIEW_REMIND, "인터뷰 리마인드 알림", LocalDateTime.now()),
					Notification.of(NoticeType.NEW_COMMENT, "새로운 댓글 알림", LocalDateTime.now()),
					Notification.of(NoticeType.NEW_LIKE, "새로운 좋아요 알림", LocalDateTime.now()),
					Notification.of(NoticeType.PUBLISH_EVENT, "출판 알림", LocalDateTime.now())
			));
		}
	}
}