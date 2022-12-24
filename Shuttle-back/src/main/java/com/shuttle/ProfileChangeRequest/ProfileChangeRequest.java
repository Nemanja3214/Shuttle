package com.shuttle.ProfileChangeRequest;


import com.shuttle.note.Note;
import com.shuttle.user.GenericUser;
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
    private GenericUser user;
    private String address;
}
