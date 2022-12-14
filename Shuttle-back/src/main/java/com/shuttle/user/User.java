package com.shuttle.user;

import com.shuttle.credentials.dto.Credentials;
import com.shuttle.note.Note;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    private String name;
    private String surname;
    private String profilePicture;
    private String telephoneNumber;
    @OneToMany
    private List<Note> notifications;
    @OneToOne
    private Credentials credentials;
    private String address;


}
