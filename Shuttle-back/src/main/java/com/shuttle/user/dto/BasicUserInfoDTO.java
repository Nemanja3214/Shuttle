package com.shuttle.user.dto;

import com.shuttle.passenger.Passenger;
import com.shuttle.user.GenericUser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasicUserInfoDTO {
	private long id;
	private String email;
	
	public static BasicUserInfoDTO from(Passenger passenger) {
		BasicUserInfoDTO dto = new BasicUserInfoDTO();
		dto.setEmail(passenger.getEmail());
		dto.setId(passenger.getId());
		return dto;
	}
	
	public BasicUserInfoDTO(GenericUser p) {
		this.id = p.getId();
		this.email = p.getEmail();
	}
}
