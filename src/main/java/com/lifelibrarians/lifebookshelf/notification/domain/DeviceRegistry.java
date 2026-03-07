package com.lifelibrarians.lifebookshelf.notification.domain;

import com.lifelibrarians.lifebookshelf.member.domain.Member;
import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "device_registries")
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeviceRegistry {

	/* 고유 정보 { */
	public static final int MAX_DEVICE_COUNT = 5;
	public static final int DEFAULT_EXPIRED_DAYS = 30;

	@Id
	@Column(nullable = false, updatable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String token;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime expiredAt;
	/* } 고유 정보 */

	/* 연관 정보 { */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;
	/* } 연관 정보 */

	/* 생성자 { */
	protected DeviceRegistry(Member member, String token, LocalDateTime createdAt,
			LocalDateTime expiredAt) {
		this.member = member;
		this.token = token;
		this.createdAt = createdAt;
		this.expiredAt = expiredAt;
	}

	public static DeviceRegistry of(Member member, String token, LocalDateTime createdAt,
			LocalDateTime expiredAt) {
		return new DeviceRegistry(member, token, createdAt, expiredAt);
	}
}
