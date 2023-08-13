package com.Test.entity;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity

public class RefreshToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String token;

	private Instant expiryDate;

	@OneToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private UserInfo userInfo;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Instant getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Instant expiryDate) {
		this.expiryDate = expiryDate;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public RefreshToken(int id, String token, Instant expiryDate, UserInfo userInfo) {
		super();
		this.id = id;
		this.token = token;
		this.expiryDate = expiryDate;
		this.userInfo = userInfo;
	}

	public RefreshToken() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "RefreshToken [id=" + id + ", token=" + token + ", expiryDate=" + expiryDate + ", userInfo=" + userInfo
				+ "]";
	}
	
	
	

}
