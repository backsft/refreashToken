package com.Test.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Test.entity.RefreshToken;
import com.Test.entity.UserInfo;


public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Integer> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUserInfo(UserInfo userInfo);
}
