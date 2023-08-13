package com.Test.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Test.entity.RefreshToken;
import com.Test.entity.UserInfo;
import com.Test.repository.RefreshTokenRepository;
import com.Test.repository.UserInfoRepository;

@Service
public class RefreshTokenService {

	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
	@Autowired
	private UserInfoRepository userInfoRepository;

	public RefreshToken createRefreshToken(String username) {

		UserInfo userInfo = userInfoRepository.findByName(username).get();
		Optional<RefreshToken> findByUserInfo = refreshTokenRepository.findByUserInfo(userInfo);

		if (findByUserInfo.isPresent()) {
			System.out.println("value present");
			RefreshToken refreshToken = findByUserInfo.get();
			refreshToken.setToken(UUID.randomUUID().toString());
			refreshToken.setExpiryDate(Instant.now().plusMillis(600000));
			refreshTokenRepository.save(refreshToken);

			return refreshToken;

		}

		else if (findByUserInfo.isEmpty()) {
			
			System.out.println("Empty");
			RefreshToken refreshToken=new RefreshToken();
			refreshToken.setToken(UUID.randomUUID().toString());
			refreshToken.setExpiryDate(Instant.now().plusMillis(600000));
			refreshToken.setUserInfo(userInfo);
			refreshTokenRepository.save(refreshToken);
			
			return refreshToken;

		}

		else
			System.out.println("none");
		throw new RuntimeException();

	}

	public Optional<RefreshToken> findByToken(String token) {
		return refreshTokenRepository.findByToken(token);
	}

	public RefreshToken verifyExpiration(RefreshToken token) {
		if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
			refreshTokenRepository.delete(token);
			throw new RuntimeException(
					token.getToken() + " Refresh token was expired. Please make a new signin request");
		}
		return token;
	}

}
