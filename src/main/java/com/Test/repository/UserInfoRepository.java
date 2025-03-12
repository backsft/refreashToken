package com.Test.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Test.entity.UserInfo;

public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {
    Optional<UserInfo> findByName(String username);
    Optional<UserInfo> findByEmail(String email);

}
