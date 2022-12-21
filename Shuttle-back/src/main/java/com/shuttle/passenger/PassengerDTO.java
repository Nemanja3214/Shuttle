package com.shuttle.passenger;

public class PassengerDTO {
	public Long id;
	public String name;
	public String surname;
	public String profilePicture;
	public String telephoneNumber;
	public String email;
	public String address;
	
	public PassengerDTO(Passenger p) {
		this.id = p.getId();
		this.name = p.getName();
		this.surname = p.getSurname();
		this.profilePicture = p.getProfilePicture();
		this.telephoneNumber = p.getTelephoneNumber();
		this.email = p.getEmail();
		this.address = p.getAddress();
	}
}
