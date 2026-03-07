package com.lifelibrarians.lifebookshelf.member.domain;

import com.lifelibrarians.lifebookshelf.app.domain.MemberAppVersions;
import com.lifelibrarians.lifebookshelf.autobiography.domain.Autobiography;
import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyStatus;
import com.lifelibrarians.lifebookshelf.community.book.domain.Book;
import com.lifelibrarians.lifebookshelf.chapter.domain.Chapter;
import com.lifelibrarians.lifebookshelf.chapter.domain.ChapterStatus;
import com.lifelibrarians.lifebookshelf.community.comment.domain.Comment;
import com.lifelibrarians.lifebookshelf.notification.domain.DeviceRegistry;
import com.lifelibrarians.lifebookshelf.notification.domain.NoticeHistory;
import com.lifelibrarians.lifebookshelf.notification.domain.NotificationSubscribe;

import java.util.List;
import java.util.Objects;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "members")
@Getter
@ToString(callSuper = true, exclude = {"memberNotificationSubscribes", "memberAutobiographies",
		"memberBooks", "memberComments", "memberChapters", "memberMemberMetadata",
		"memberNoticeHistories", "memberChapterStatuses", "socialMember", "passwordMember",
		"memberDeviceRegistries"}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

	/* 고유 정보 { */
	@Id
	@Column(nullable = false, updatable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private LoginType loginType;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false, name = "\"role\"")
	@Enumerated(EnumType.STRING)
	private MemberRole role;

	/**
	 * V2에서 더 이상 사용하지 않는 필드
	 */
	@Deprecated
	@Column
	private String profileImageUrl;

	/**
	 * V2에서 더 이상 사용하지 않는 필드
	 */
	@Deprecated
	@Column(nullable = false)
	private String nickname;

	/**
	 * V2에서 더 이상 사용하지 않는 필드
	 */
	@Deprecated
	@Column(nullable = false)
	private LocalDateTime nicknameUpdatedAt;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	@Column
	private LocalDateTime deletedAt;
	/* } 고유 정보 */

	/* 연관 정보 { */
	@OneToMany(mappedBy = "member")
	private Set<NotificationSubscribe> memberNotificationSubscribes;

	@OneToMany(mappedBy = "member")
	private Set<Autobiography> memberAutobiographies;

	@OneToMany(mappedBy = "member")
	private Set<Book> memberBooks;

	@OneToMany(mappedBy = "member")
	private Set<Comment> memberComments;

	@OneToMany(mappedBy = "member")
	@Deprecated
	private Set<Chapter> memberChapters;

	@OneToOne(mappedBy = "member")
	private MemberMetadata memberMemberMetadata;

	@OneToMany(mappedBy = "member")
	private Set<NoticeHistory> memberNoticeHistories;

	@OneToMany(mappedBy = "member")
	@Deprecated
	private Set<ChapterStatus> memberChapterStatuses;

	@OneToMany(mappedBy = "member")
	private Set<DeviceRegistry> memberDeviceRegistries;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "social_member_id", unique = true)
	private SocialMember socialMember;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "password_member_id", unique = true)
	private PasswordMember passwordMember;
	/* } 연관 정보 */
    /* { V2 정보 */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member", unique = true)
    private AutobiographyStatus memberAutobiographyStatus;

    @OneToMany(mappedBy = "member")
    private List<MemberAppVersions> memberAppVersions;
    /* } V2 정보 */

	/* 생성자 (V1) { */
	@Deprecated
	protected Member(LoginType loginType, String email, MemberRole role, String profileImageUrl,
					 String nickname, LocalDateTime createdAt, LocalDateTime nicknameUpdatedAt,
					 LocalDateTime deletedAt) {
		this.loginType = loginType;
		this.email = email;
		this.role = role;
		this.profileImageUrl = profileImageUrl;
		this.nickname = nickname;
		this.createdAt = createdAt;
		this.nicknameUpdatedAt = nicknameUpdatedAt;
		this.deletedAt = deletedAt;
	}

	@Deprecated
	public static Member of(LoginType loginType, String email, MemberRole role,
							String profileImageUrl,
							String nickname, LocalDateTime createdAt, LocalDateTime nicknameUpdatedAt,
							LocalDateTime deletedAt) {
		return new Member(loginType, email, role, profileImageUrl, nickname, createdAt,
				nicknameUpdatedAt, deletedAt);
	}
	/* } 생성자 (V1) */

	/* 생성자 (V2) { */
	/**
	 * V2용 생성자 – nickname / nicknameUpdatedAt / profileImageUrl 을 더 이상 외부에서 사용하지 않는다.
	 * 내부적으로는 NOT NULL 제약을 맞추기 위해 기본값을 세팅한다.
	 */
	protected Member(LoginType loginType, String email, MemberRole role,
					 LocalDateTime createdAt, LocalDateTime deletedAt) {
		this.loginType = loginType;
		this.email = email;
		this.role = role;
		this.createdAt = createdAt;
		this.deletedAt = deletedAt;

		// V2에서 논리적으로는 사용하지 않지만, DB 제약 때문에 기본값 세팅
		this.profileImageUrl = null;
		this.nickname = email; // 혹은 "user" 등 프로젝트 규칙에 맞게 변경 가능
		this.nicknameUpdatedAt = createdAt != null ? createdAt : LocalDateTime.now();
	}

	/**
	 * V2용 팩토리 메서드 – 새 코드에서는 이 메서드만 사용
	 */
	public static Member ofV2(LoginType loginType, String email, MemberRole role,
							  LocalDateTime createdAt, LocalDateTime deletedAt) {
		LocalDateTime base = createdAt != null ? createdAt : LocalDateTime.now();
		return new Member(loginType, email, role, base, deletedAt);
	}
	/* } 생성자 (V2) */

	/* 연관 관계 편의 메서드 { */
	public void addSocialMember(SocialMember socialMember) {
		this.socialMember = socialMember;
	}

	public void addPasswordMember(PasswordMember passwordMember) {
		this.passwordMember = passwordMember;
	}

	public void softDelete(LocalDateTime now) {
		this.deletedAt = now;
	}

	public void setMemberMemberMetadata(MemberMetadata memberMetadata) {
		this.memberMemberMetadata = memberMetadata;
		memberMetadata.setMember(this);
	}

	@Deprecated
	public void changeDefaultProfileImage() {
		this.profileImageUrl = null;
	}

	@Deprecated
	public boolean isEqualProfileImageUrl(String profileImageUrl) {
		if (Objects.isNull(this.profileImageUrl)) {
			return Objects.isNull(profileImageUrl);
		}
		return profileImageUrl.equals(this.profileImageUrl);
	}

	@Deprecated
	public void updateProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}

	@Deprecated
	public void updateNickname(String nickname, LocalDateTime now) {
		if (nickname == null || nickname.isEmpty() || nickname.equals(this.nickname)) {
			return;
		}
		this.nickname = nickname;
		this.nicknameUpdatedAt = now;
	}

	public boolean isAdmin() {
		return this.role == MemberRole.ADMIN;
	}

    public void restore() {
        this.deletedAt = null;
    }
    /* } 연관 관계 편의 메서드 */
}
