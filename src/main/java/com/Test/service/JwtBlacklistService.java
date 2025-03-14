package com.Test.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class JwtBlacklistService {
	private final StringRedisTemplate redisTemplate;

	public JwtBlacklistService(StringRedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	// Add token to blacklist with expiration time
	public void blacklistToken(String token, long expirationMillis) {
		redisTemplate.opsForValue().set(token, "blacklisted", expirationMillis, TimeUnit.MILLISECONDS);
	}

	// Check if token is blacklisted
	public boolean isTokenBlacklisted(String token) {
		return redisTemplate.hasKey(token);
	}
}
