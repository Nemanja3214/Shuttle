package com.shuttle.user.dto;

public class BasicUserInfoDTO {
	private long id;
	private String email;
	
	
	
	public BasicUserInfoDTO() {
		super();
	}

	public BasicUserInfoDTO(long id, String email) {
		super();
		this.id = id;
		this.email = email;
	}
	
	public static BasicUserInfoDTO getMock() {
		return new BasicUserInfoDTO(123, "user@example.com");
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	

}
