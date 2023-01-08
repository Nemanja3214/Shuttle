package com.shuttle.user.dto;

import com.shuttle.user.GenericUser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTONoPassword {
    private long id;
    private String name;
    private String surname;
    private String profilePicture;
    private String telephoneNumber;
    private String email;
    private String address;

    public UserDTONoPassword(GenericUser user) {
        this.id = user.getId();
        this.name = user.getName();
        this.surname = user.getSurname();
        this.email = user.getEmail();
        this.telephoneNumber = user.getTelephoneNumber();
        this.address = user.getAddress();
        this.profilePicture = user.getPassword();
    }
}
