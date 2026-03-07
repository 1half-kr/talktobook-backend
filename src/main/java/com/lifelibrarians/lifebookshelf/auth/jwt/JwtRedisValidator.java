package com.lifelibrarians.lifebookshelf.auth.jwt;

import com.lifelibrarians.lifebookshelf.config.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtRedisValidator {

    @Qualifier("refreshTokenRedisTemplate")
    private final RedisTemplate<String, String> refreshTokenRedisTemplate;

    public boolean isValidSession(Jwt jwt) {
        Long memberId = jwt.getClaim(JwtProperties.MEMBER_ID);
        if (memberId == null) {
            return false;
        }

        String memberIdStr = memberId.toString();
        String accessKey = "access:" + memberIdStr;
        String refreshKey = "refresh:" + memberIdStr;
        
        // Access Token이 Redis에 저장된 것과 일치하는지 확인
        String storedAccessToken = refreshTokenRedisTemplate.opsForValue().get(accessKey);
        if (storedAccessToken == null || !storedAccessToken.equals(jwt.getTokenValue())) {
            return false;
        }
        
        // Refresh Token도 존재하는지 확인 (세션 유효성)
        return refreshTokenRedisTemplate.hasKey(refreshKey);
    }
}
