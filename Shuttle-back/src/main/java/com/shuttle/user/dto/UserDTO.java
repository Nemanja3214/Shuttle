package com.shuttle.user.dto;

import com.shuttle.user.GenericUser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private long id;
    private String name;
    private String surname;
    private String profilePicture;
    private String telephoneNumber;
    private String email;
    private String address;
    private String password;

    public UserDTO(GenericUser user) {
        this.id = user.getId();
        this.name = user.getName();
        this.surname = user.getSurname();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.telephoneNumber = user.getTelephoneNumber();
        this.address = user.getAddress();
        this.profilePicture = user.getPassword();
    }

    public UserDTO(long id, String name, String surname, String profilePicture, String telephoneNumber, String email,
                   String address) {
        super();
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.profilePicture = profilePicture;
        this.telephoneNumber = telephoneNumber;
        this.email = email;
        this.address = address;
    }

    public static UserDTO getMock() {
        return new UserDTO(10, "Pera", "Perić", "U3dhZ2dlciByb2Nrcw==", "+381123123", "pera.peric@email.com", "Bulevar Oslobodjenja 74");
    }


}
