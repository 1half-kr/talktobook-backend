package com.lifelibrarians.lifebookshelf.auth.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TemporaryUser {
	private String email;
	private String password;
	private String code;
	private LocalDateTime expiresAt;

    @JsonIgnore
	public boolean isExpired() {
		return LocalDateTime.now().isAfter(expiresAt);
	}

	public boolean matchCode(String inputCode) {
		return code.equals(inputCode);
	}
}
