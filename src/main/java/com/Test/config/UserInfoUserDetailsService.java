package com.Test.config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.Test.entity.UserInfo;
import com.Test.repository.UserInfoRepository;

@Component
public class UserInfoUserDetailsService implements UserDetailsService {

	@Autowired
	private UserInfoRepository repository;

	/*
	 * @Override public UserDetails loadUserByUsername(String username) throws
	 * UsernameNotFoundException { Optional<UserInfo> userInfo =
	 * repository.findByName(username); return
	 * userInfo.map(UserInfoUserDetails::new) .orElseThrow(() -> new
	 * UsernameNotFoundException("user not found " + username));
	 * 
	 * }
	 */

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		Optional<UserInfo> userInfo = repository.findByEmail(email);
		if (userInfo.isPresent()) {
			UserInfo user = userInfo.get();
			if (!user.isEnabled()) {
				throw new DisabledException("User is not enabled");
			}
			return new UserInfoUserDetails(user);
		} else {
			throw new UsernameNotFoundException("User not found: " + email);
		}

	}

}
