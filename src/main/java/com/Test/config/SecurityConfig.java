package com.Test.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.Test.filter.JwtAuthFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	@Autowired
	private JwtAuthFilter authFilter;

	@Bean
	UserDetailsService userDetailsService() {
		return new UserInfoUserDetailsService();
	}

//	@Bean
//	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//		return http.csrf(csrf -> csrf.disable())
//				.authorizeHttpRequests(
//						requests -> requests.requestMatchers("/api/login", "/api/refreshToken").permitAll()
//
//								.requestMatchers("/products/signUp").hasRole("SUPERADMIN") // Only SUPERADMIN can sign
//																							// up users
//								.requestMatchers("/", "/api/logout").authenticated() // Other endpoints require
//																						// authentication
//				).sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
//				.authenticationProvider(authenticationProvider())
//				.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class).build();
//	}
//	
//	
	
//	@Bean
//	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//	    return http
//	            .cors(cors -> cors.configurationSource(request -> {
//	                CorsConfiguration config = new CorsConfiguration();
//	                config.setAllowedOrigins(List.of("http://127.0.0.1:5501")); // Allow frontend origin
//	                config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//	                config.setAllowedHeaders(List.of("*"));
//	                config.setAllowCredentials(true);
//	                return config;
//	            }))
//	            .csrf(csrf -> csrf.disable()) // Disable CSRF if needed
//	            .authorizeHttpRequests(requests -> requests
//	                    .requestMatchers("/api/login", "/api/refreshToken").permitAll()
//	                    .requestMatchers("/products/signUp").hasRole("SUPERADMIN")
//	                    .requestMatchers("/", "/api/logout").authenticated()
//	            )
//	            .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
//	            .authenticationProvider(authenticationProvider())
//	            .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
//	            .build();
//	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    return http
	            .cors(cors -> cors.configurationSource(request -> {
	                CorsConfiguration config = new CorsConfiguration();
	                config.setAllowedOrigins(List.of("*")); // Allow all origins
	                config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
	                config.setAllowedHeaders(List.of("*"));
	                config.setAllowCredentials(false); // Set to false if allowing all origins
	                return config;
	            }))
	            .csrf(csrf -> csrf.disable()) // Disable CSRF if needed
	            .authorizeHttpRequests(requests -> requests
	                    .requestMatchers("/api/login", "/api/refreshToken").permitAll()
	                    .requestMatchers("/products/signUp").hasRole("SUPERADMIN")
	                    .requestMatchers("/", "/api/logout").authenticated()
	            )
	            .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
	            .authenticationProvider(authenticationProvider())
	            .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
	            .build();
	}


	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService());
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

}
