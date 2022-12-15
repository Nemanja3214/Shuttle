package com.shuttle.driver;

import lombok.Data;

@Data
public class DriverDTO {
    Long id;
    private String name;
    private String surname;
    private String profilePicture;
    private String telephoneNumber;
    private String email;
    private String address;

    public DriverDTO(Long id, String name, String surname, String profilePicture, String telephoneNumber, String email, String address) {
        this.id=id;
        this.name = name;
        this.surname = surname;
        this.profilePicture = profilePicture;
        this.telephoneNumber = telephoneNumber;
        this.email = email;
        this.address = address;
    }

    public DriverDTO() {
    }

    public static DriverDTO parse2DTO(Driver driver) {
        return new DriverDTO(driver.getId(), driver.getName(), driver.getSurname(), driver.getProfilePicture(),
                driver.getTelephoneNumber(), driver.getCredentials().getEmail(), driver.getAddress());
    }
}
