package com.shuttle.driver;

import com.shuttle.common.Entity;
import com.shuttle.user.UserDTO;
import lombok.Getter;
import lombok.Setter;

public class DriverDTO {
    @Getter
    @Setter
    Long id;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String surname;
    @Getter
    @Setter
    private String profilePicture;
    @Getter
    @Setter
    private String telephoneNumber;
    @Getter
    @Setter
    private String email;
    @Getter
    @Setter
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
                driver.getTelephoneNumber(), driver.getEmail(), driver.getAddress());
    }
}
