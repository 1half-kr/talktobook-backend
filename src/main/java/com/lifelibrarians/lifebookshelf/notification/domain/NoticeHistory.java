package com.lifelibrarians.lifebookshelf.notification.domain;

import com.lifelibrarians.lifebookshelf.member.domain.Member;
import javax.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "noticeHistories")
@Getter
@ToString(callSuper = true, exclude = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeHistory {

	/* 고유 정보 { */
	@Id
	@Column(nullable = false, updatable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	@Lob
	private String content;

	@Column(nullable = false)
	private LocalDateTime receivedAt;

	@Column(nullable = false)
	private Boolean isRead;
	/* } 고유 정보 */

	/* 연관 정보 { */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;
	/* } 연관 정보 */

	/* 생성자 { */
	protected NoticeHistory(String title, String content, LocalDateTime receivedAt,
			Boolean isRead) {
		this.title = title;
		this.content = content;
		this.receivedAt = receivedAt;
		this.isRead = isRead;
	}

	public static NoticeHistory of(String title, String content, LocalDateTime receivedAt,
			Boolean isRead) {
		return new NoticeHistory(title, content, receivedAt, isRead);
	}
	/* } 생성자 */
}
