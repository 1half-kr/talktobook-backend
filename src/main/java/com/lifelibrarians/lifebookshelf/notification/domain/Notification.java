package com.lifelibrarians.lifebookshelf.notification.domain;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "notifications")
@Getter
@ToString(callSuper = true, exclude = "notificationNotificationSubscribes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

	/* 고유 정보 { */
	@Id
	@Column(nullable = false, updatable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private NoticeType noticeType;

	@Column(name = "\"description\"")
	private String description;

	@Column(nullable = false)
	private LocalDateTime createdAt;
	/* } 고유 정보 */

	/* 연관 정보 { */
	@OneToMany(mappedBy = "notification")
	private Set<NotificationSubscribe> notificationNotificationSubscribes;
	/* } 연관 정보 */

	/* 생성자 { */
	protected Notification(NoticeType noticeType, String description, LocalDateTime createdAt) {
		this.noticeType = noticeType;
		this.description = description;
		this.createdAt = createdAt;
	}

	public static Notification of(NoticeType noticeType, String description,
			LocalDateTime createdAt) {
		return new Notification(noticeType, description, createdAt);
	}
	/* } 생성자 */
}
