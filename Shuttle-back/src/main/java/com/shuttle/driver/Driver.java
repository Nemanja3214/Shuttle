package com.shuttle.driver;

import com.shuttle.user.User;

public class Driver extends User {

    public Driver() {
    }

    public Driver(Long id, String name, String surname, String profilePicture, String telephoneNumber, String email, String address, String password) {
        super(id, name, surname, profilePicture, telephoneNumber, email, address, password);
    }


}
