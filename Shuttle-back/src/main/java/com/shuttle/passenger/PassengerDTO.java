package com.shuttle.passenger;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class PassengerDTO {
	public Long id;
	public String name;
	public String surname;
	public String profilePicture;
	public String telephoneNumber;
	public String email;
	public String address;
	public String password;
	
	public PassengerDTO(Passenger p) {
		this.id = p.getId();
		this.name = p.getName();
		this.surname = p.getSurname();
		this.profilePicture = p.getProfilePicture();
		this.telephoneNumber = p.getTelephoneNumber();
		this.email = p.getEmail();
		this.address = p.getAddress();
		this.password = p.getPassword();
	}
	
	public static Passenger from(PassengerDTO dto) {
		Passenger p = new Passenger();
		p.setId(dto.getId());
		p.setName(dto.getName());
		p.setSurname(dto.getSurname());
		p.setProfilePicture(dto.getProfilePicture());
		p.setTelephoneNumber(dto.getTelephoneNumber());
		p.setEmail(dto.getEmail());
		p.setAddress(dto.getAddress());
		p.setPassword(dto.getPassword());
		return p;
	}
}
