package com.shuttle.credentials.dto;

public class TokenDTO {
	private String accessToken;
	private String refreshToken;
	
	
	
	public TokenDTO() {
		super();
	}
	
	public TokenDTO(String accessToken, String refreshToken) {
		super();
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}
	public static TokenDTO getMock() {
		return new TokenDTO("asd", "asd");
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
	

}
