package com.shuttle.user;

import com.shuttle.common.Entity;
import com.shuttle.credentials.dto.Credentials;
import com.shuttle.note.Note;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.management.Notification;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@jakarta.persistence.Entity
public class User extends Entity {

    private String name;
    private String surname;
    private String profilePicture;
    private String telephoneNumber;
    @OneToMany
    private List<Note> notifications;
	@OneToOne
    private Credentials credentials;
    private String address;


    public User(Long id, String name, String surname, String profilePicture, String telephoneNumber, Credentials credentials, String address) {
        this.name = name;
        this.surname = surname;
        this.profilePicture = profilePicture;
        this.telephoneNumber = telephoneNumber;
        this.credentials = credentials;
        this.address = address;
        this.setId(id);
    }


}
