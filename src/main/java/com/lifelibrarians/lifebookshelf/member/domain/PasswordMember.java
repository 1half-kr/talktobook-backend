package com.lifelibrarians.lifebookshelf.member.domain;

import javax.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Entity
@Table(name = "passwordMembers")
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PasswordMember {

	/* 고유 정보 { */
	@Id
	@Column(nullable = false, updatable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String password;
	/* } 고유 정보 */

	/* 연관 정보 { */
	/* } 연관 정보 */

	/* 생성자 { */
	protected PasswordMember(String password) {
		this.password = password;
	}

	public static PasswordMember of(String password) {
		return new PasswordMember(password);
	}

	/* } 생성자 */
	
	public boolean matchPassword(String password) {
		return this.password.equals(password);
	}

	public void updatePassword(String password) {
		this.password = password;
	}
}
