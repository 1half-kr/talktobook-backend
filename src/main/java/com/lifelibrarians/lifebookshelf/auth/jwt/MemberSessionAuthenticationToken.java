package com.lifelibrarians.lifebookshelf.auth.jwt;

import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class MemberSessionAuthenticationToken extends AbstractAuthenticationToken {

	private final Object principal;

	public MemberSessionAuthenticationToken(
			Object principal,
			Collection<? extends GrantedAuthority> authorities
	) {
		super(authorities);
		this.principal = principal;
	}

	@Override
	public Object getCredentials() {
		return principal;
	}

	@Override
	public Object getPrincipal() {
		return principal;
	}
}