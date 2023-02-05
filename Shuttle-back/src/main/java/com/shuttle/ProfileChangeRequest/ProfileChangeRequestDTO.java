package com.shuttle.ProfileChangeRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileChangeRequestDTO {
	public ProfileChangeRequestDTO(ProfileChangeRequest request) {
		this.address = request.getAddress();
		this.name = request.getAddress();
		this.surname = request.getSurname();
		this.profilePicture = request.getProfilePicture();
		this.telephoneNumber = request.getTelephoneNumber();
	}
	private String name;
    private String surname;
    private String profilePicture;
    private String telephoneNumber;
    private String address;
}
