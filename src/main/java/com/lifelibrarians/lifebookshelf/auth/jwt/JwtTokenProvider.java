package com.lifelibrarians.lifebookshelf.auth.jwt;

import com.lifelibrarians.lifebookshelf.config.JwtProperties;
import com.lifelibrarians.lifebookshelf.exception.status.AuthExceptionStatus;
import com.lifelibrarians.lifebookshelf.member.domain.MemberRole;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

	private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
	private final JwtProperties jwtProperties;
//	private final static String TOKEN_TYPE = "typ";

	/**
	 * memberId를 받아서 accessToken을 생성한다.
	 *
	 * @param memberId 회원 고유 ID
	 * @return accessToken
	 */
	public Jwt createMemberAccessToken(Long memberId) {
		Instant now = Instant.now();
		JwsHeader header = JwsHeader
				.with(MacAlgorithm.HS256)
				.build();
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuedAt(now)
				.expiresAt(now.plusSeconds(jwtProperties.getAccessTokenExpirationTime()))
				.claim(JwtProperties.MEMBER_ID, memberId)
				.claim(JwtProperties.ROLES, List.of(MemberRole.MEMBER.name()))
				.build();
		return jwtEncoder.encode(JwtEncoderParameters.from(header, claims));
	}

	/**
	 * memberId를 받아서 refreshToken을 생성한다.
	 *
	 * @param memberId 회원 고유 ID
	 * @return refreshToken
	 */
	public Jwt createMemberRefreshToken(Long memberId) {
		Instant now = Instant.now();
		JwsHeader header = JwsHeader
				.with(MacAlgorithm.HS256)
				.build();
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuedAt(now)
				.expiresAt(now.plusSeconds(jwtProperties.getRefreshTokenExpirationTime()))
				.claim(JwtProperties.MEMBER_ID, memberId)
				.build();
		return jwtEncoder.encode(JwtEncoderParameters.from(header, claims));
	}

    // 정상적인 서명 키인지 검증
    public Jwt parseToken(String tokenValue) {
        try {
            return jwtDecoder.decode(tokenValue);
        } catch (JwtException e) {
            throw AuthExceptionStatus.INVALID_AUTH_CODE.toServiceException();
        }
    }

    // 만료 시간 검증
    public void validateRefreshToken(Jwt jwt) {
        Instant expiresAt = jwt.getExpiresAt();
        if (expiresAt == null || expiresAt.isBefore(Instant.now())) {
            throw AuthExceptionStatus.REFRESH_TOKEN_EXPIRED.toServiceException();
        }
    }



    // Refresh Token에서 memberId 추출
    public Long extractMemberIdFromRefreshToken(Jwt jwt) {
        Object id = jwt.getClaim(JwtProperties.MEMBER_ID);
        if (id == null) {
            throw AuthExceptionStatus.INVALID_REFRESH_TOKEN.toServiceException();
        }
        return Long.valueOf(id.toString());
    }
}