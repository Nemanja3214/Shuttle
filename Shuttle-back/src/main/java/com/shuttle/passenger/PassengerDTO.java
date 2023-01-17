package com.shuttle.passenger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
		p.setProfilePicture(p.getProfilePicture());
		p.setTelephoneNumber(dto.getTelephoneNumber());
		p.setEmail(dto.getEmail());
		p.setAddress(dto.getAddress());
		p.setPassword(dto.getPassword());
		return p;
	}
	
//	@JsonIgnore
//	public boolean isInvalid() {
//		return !isEmailValid() || !isPhoneValid() || !isPasswordValid() || hasEmptyField();
//	}
//	
	public boolean hasEmptyField() {
		return this.name.equals("") || 
				this.surname.equals("") ||
				this.telephoneNumber.equals("") ||
				this.email.equals("") ||
				this.address.equals("") ||
				this.password.equals("");
	}
	
	@JsonIgnore
	private boolean isEmailValid() {
		String regex = "^[A-Za-z0-9+_.-]+@(.+)$";  
		Pattern pattern = Pattern.compile(regex);  	
		Matcher matcher = pattern.matcher(email);  
		return matcher.matches();
	}
	
	@JsonIgnore
	private boolean isPhoneValid() {
		String regex = "^[\\+]?[0-9]+$";
		Pattern pattern = Pattern.compile(regex);  	
		Matcher matcher = pattern.matcher(telephoneNumber);  
		return matcher.matches();
	}
	
	@JsonIgnore
	private boolean isPasswordValid() {
		String regex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
		Pattern pattern = Pattern.compile(regex);  	
		Matcher matcher = pattern.matcher(password);  
		return matcher.matches();
	}
	
	
	
	
	
}
