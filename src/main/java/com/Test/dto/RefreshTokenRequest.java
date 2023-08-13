package com.Test.dto;

public class RefreshTokenRequest {

	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public RefreshTokenRequest(String token) {
		super();
		this.token = token;
	}

	public RefreshTokenRequest() {

	}

	@Override
	public String toString() {
		return "RefreshTokenRequest [token=" + token + "]";
	}

}
