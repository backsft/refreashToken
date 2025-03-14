package com.Test.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Test.dto.UserSignupRequest;
import com.Test.entity.UserInfo;
import com.Test.repository.UserInfoRepository;

@Service
public class UserService {

	@Autowired
	private UserInfoRepository userInfoRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public UserInfo registerUser(UserSignupRequest request) {
		// Check if email already exists
		if (userInfoRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new RuntimeException("Email is already taken");
		}

		// Create new user
		UserInfo user = new UserInfo();
		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword())); // Encrypt password
		user.setRole(request.getRole());
		user.setEnabled(true);

		return userInfoRepository.save(user);
	}

}
