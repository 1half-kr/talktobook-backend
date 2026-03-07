package com.lifelibrarians.lifebookshelf.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class JwtProperties {

	@Value("${jwt.expiration.access-token}")
	private long accessTokenExpirationTime;

	@Value("${jwt.expiration.refresh-token}")
	private long refreshTokenExpirationTime;

	public static final String ROLES = "roles";
	public static final String SCOPES = "scopes";
	public static final String MEMBER_ID = "memberId";
}
