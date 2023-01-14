package com.shuttle.user.dto;

import com.shuttle.passenger.Passenger;

public class BasicUserInfoDTO {
	private long id;
	private String email;
	
	public static BasicUserInfoDTO from(Passenger passenger) {
		BasicUserInfoDTO dto = new BasicUserInfoDTO();
		dto.setEmail(passenger.getEmail());
		dto.setId(passenger.getId());
		return dto;
	}
	
	
	
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
