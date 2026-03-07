package com.lifelibrarians.lifebookshelf.auth.service;

import com.lifelibrarians.lifebookshelf.auth.domain.TemporaryUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@Log4j2
public class TemporaryUserStore {
    private final RedisTemplate<String, TemporaryUser> redisTemplate;

    public TemporaryUserStore(
            @Qualifier("temporaryUserRedisTemplate")
            RedisTemplate<String, TemporaryUser> redisTemplate
    ) {
        this.redisTemplate = redisTemplate;
    }

    private final String PREFIX = "temp-user:";

    public void save(String email, TemporaryUser user) {
        redisTemplate.opsForValue().set(PREFIX + email, user, Duration.ofMinutes(5));
    }

    public Optional<TemporaryUser> find(String email) {
        TemporaryUser user = redisTemplate.opsForValue().get(PREFIX + email);
        return Optional.ofNullable(user);
    }

    public void remove(String email) {
        redisTemplate.delete(PREFIX + email);
    }
}
