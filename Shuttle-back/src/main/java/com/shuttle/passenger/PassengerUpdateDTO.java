package com.shuttle.passenger;

import lombok.Data;

@Data
public class PassengerUpdateDTO {
    private String name;
    private String surname;
    private String profilePicture;
    private String telephoneNumber;
    private String email;
    private String address;
}
