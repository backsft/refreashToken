package com.Test.controller;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.Test.dto.AuthRequest;
import com.Test.dto.JwtResponse;
import com.Test.dto.RefreshTokenRequest;
import com.Test.dto.UserSignupRequest;
import com.Test.entity.RefreshToken;
import com.Test.service.JwtService;
import com.Test.service.RefreshTokenService;
import com.Test.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private RefreshTokenService refreshTokenService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@PostMapping("/login")
	public JwtResponse authenticateAndGetToken(@RequestBody AuthRequest authRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
		if (authentication.isAuthenticated()) {
			// generate refresh token
			RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequest.getEmail());

			// generate access token
			String accessToken = jwtService.generateToken(authRequest.getEmail());
			System.out.println("accessToken is " + accessToken);
			// JwtResponse jwtResponse=new JwtResponse(accessToken,
			// refreshToken.getToken());

			JwtResponse jwtResponse = new JwtResponse();

			jwtResponse.setAccessToken(accessToken);
			jwtResponse.setRefreashToken(refreshToken.getToken());

			return jwtResponse;

		} else {
			throw new UsernameNotFoundException("invalid user request !");
		}
	}

	@PostMapping("/refreshToken")
	public JwtResponse refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {

		return refreshTokenService.findByToken(refreshTokenRequest.getToken())
				.map(refreshTokenService::verifyExpiration).map(RefreshToken::getUserInfo).map(userInfo -> {
					String accessToken = jwtService.generateToken(userInfo.getEmail());

					JwtResponse jwtResponse = new JwtResponse(accessToken, refreshTokenRequest.getToken());
					return jwtResponse;

				}).orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
	}

	@PostMapping("/logout")
	public ResponseEntity<String> logout(HttpServletRequest request) {
		String token = request.getHeader("Authorization");

		System.out.println("token " + token);
		if (token != null && token.startsWith("Bearer ")) {
			token = token.replace("Bearer ", "");
			jwtService.forceExpireToken(token);
			return ResponseEntity.ok("Token has been force expired.");
		}
		return ResponseEntity.badRequest().body("Invalid token.");
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(LocalDate.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) throws IllegalArgumentException {
				setValue(LocalDate.parse(text, DateTimeFormatter.ofPattern("dd-MM-yyyy")));
			}
		});
	}

	@PostMapping("/signup")
	public ResponseEntity<String> signup(@RequestParam String name, @RequestParam String email,
			@RequestParam String password, @RequestParam String role,
			@RequestParam(required = false) String drivingLicenseNumber,
			@RequestParam(required = false) String drivingLicenseExpiryDate, // Pass as String
			@RequestParam(required = false) String carModel,
			@RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture) throws IOException {

		// Convert the date string to LocalDate
		LocalDate expiryDate = null;
		if (drivingLicenseExpiryDate != null) {
			expiryDate = LocalDate.parse(drivingLicenseExpiryDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		}

		// Create the UserSignupRequest object
		UserSignupRequest signupRequest = new UserSignupRequest();
		signupRequest.setName(name);
		signupRequest.setEmail(email);
		signupRequest.setPassword(password);
		signupRequest.setRole(role);
		signupRequest.setDrivingLicenseNumber(drivingLicenseNumber);
		signupRequest.setDrivingLicenseExpiryDate(expiryDate);
		signupRequest.setCarModel(carModel);

		// Pass the request object to the service layer
		return userService.signup(signupRequest, profilePicture);
	}

}
