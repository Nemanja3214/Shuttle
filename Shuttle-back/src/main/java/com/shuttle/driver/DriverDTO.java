package com.shuttle.driver;

import com.shuttle.user.UserDTO;

public class DriverDTO extends UserDTO {
    public DriverDTO(Long id, String name, String surname, String profilePicture, String telephoneNumber, String email, String address) {
        super(id, name, surname, profilePicture, telephoneNumber, email, address);
    }
}
