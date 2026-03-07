package com.lifelibrarians.lifebookshelf.auth.jwt;

import com.lifelibrarians.lifebookshelf.auth.dto.MemberSessionDto;
import com.lifelibrarians.lifebookshelf.auth.exception.JwtAuthenticationTokenException;
import com.lifelibrarians.lifebookshelf.config.JwtProperties;
import com.lifelibrarians.lifebookshelf.member.domain.MemberRole;
import java.io.IOException;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

@Log4j2
@RequiredArgsConstructor
public class MemberSessionAuthenticationFilter extends OncePerRequestFilter {

	private final JwtRedisValidator jwtRedisValidator;

	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication authentication = context.getAuthentication();
		if (authentication == null) {
			doFilter(request, response, filterChain);
			return;
		}
		try {
			if (authentication instanceof JwtAuthenticationToken) {
				JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
				MemberSessionAuthenticationToken token = convert(jwtAuthenticationToken);
				context.setAuthentication(token);
				SecurityContextHolder.clearContext();
				SecurityContextHolder.setContext(context);
			}
		} catch (JwtAuthenticationTokenException e) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
			return;
		}
		doFilter(request, response, filterChain);
	}

	private MemberSessionAuthenticationToken convert(JwtAuthenticationToken token) {
		Object target = token.getPrincipal();
		if (target instanceof Jwt) {
			Jwt jwt = (Jwt) target;
			
			// Redis 세션 검증 추가
			if (!jwtRedisValidator.isValidSession(jwt)) {
				throw new JwtAuthenticationTokenException("세션이 만료되었거나 로그아웃된 사용자입니다.");
			}
			
			MemberSessionDto principal = convertPrincipal(jwt);
			MemberSessionAuthenticationToken ret = new MemberSessionAuthenticationToken(principal,
					token.getAuthorities());
			ret.setAuthenticated(true);
			log.info("convert token: principal{}, authorities {}", ret.getPrincipal(),
					token.getAuthorities());
			return ret;
		}
		throw new JwtAuthenticationTokenException("JwtAuthenticationToken의 Principal이 Jwt가 아닙니다.");
	}

	private MemberSessionDto convertPrincipal(Jwt jwt) {
		Long userId = jwt.getClaim(JwtProperties.MEMBER_ID);
        log.info("Jwt Member ID: {}", userId);
		List<MemberRole> roles = jwt.getClaim(JwtProperties.ROLES);
		if (userId == null || roles == null) {
			throw new JwtAuthenticationTokenException("Jwt에 필요한 정보가 없습니다.");
		}
		return MemberSessionDto.builder()
				.memberId(userId)
				.roles(roles)
				.build();
	}
}

