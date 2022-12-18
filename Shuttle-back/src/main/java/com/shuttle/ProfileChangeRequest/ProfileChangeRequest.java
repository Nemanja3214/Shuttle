package com.shuttle.ProfileChangeRequest;

import com.shuttle.credentials.Credentials;
import com.shuttle.note.Note;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Entity
public class ProfileChangeRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    private String name;
    private String surname;
    private String profilePicture;
    private String telephoneNumber;
    @OneToOne
    private Credentials credentials;
    private String address;
}
