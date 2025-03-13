package com.Test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.Test.dto.AuthRequest;
import com.Test.dto.JwtResponse;
import com.Test.dto.RefreshTokenRequest;
import com.Test.entity.RefreshToken;
import com.Test.service.JwtService;
import com.Test.service.RefreshTokenService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
//@CrossOrigin(origins = {"http://localhost:5173"})
public class Logout {

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

//	@PostMapping("/logout")
//	public ResponseEntity<String> logout(HttpServletRequest request) {
//		String token = request.getHeader("Authorization").replace("Bearer ", "");
//		jwtService.forceExpireToken(token);
//		return ResponseEntity.ok("Token has been force expired.");
//	}
	
	  @PostMapping("/logout")
	    public ResponseEntity<String> logout(HttpServletRequest request) {
	        String token = request.getHeader("Authorization");
	        
	        System.out.println("token "+token);
	        if (token != null && token.startsWith("Bearer ")) {
	            token = token.replace("Bearer ", "");
	            jwtService.forceExpireToken(token);
	            return ResponseEntity.ok("Token has been force expired.");
	        }
	        return ResponseEntity.badRequest().body("Invalid token.");
	    }

	    @RequestMapping(value = "/logout", method = RequestMethod.OPTIONS)
	    public ResponseEntity<?> handleOptions() {
	        return ResponseEntity.ok().build();
	    }

}
