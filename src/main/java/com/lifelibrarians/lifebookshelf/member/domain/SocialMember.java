package com.lifelibrarians.lifebookshelf.member.domain;

import javax.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "SocialMembers")
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialMember {

	/* 고유 정보 { */
	@Id
	@Column(nullable = false, updatable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String oauthId;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private OauthType oauthType;
	/* } 고유 정보 */

	/* 연관 정보 { */
	/* } 연관 정보 */

	/* 생성자 { */
	protected SocialMember(String oauthId, OauthType oauthType) {
		this.oauthId = oauthId;
		this.oauthType = oauthType;
	}

	public static SocialMember of(String oauthId, OauthType oauthType) {
		return new SocialMember(oauthId, oauthType);
	}

	/* } 생성자 */
}
