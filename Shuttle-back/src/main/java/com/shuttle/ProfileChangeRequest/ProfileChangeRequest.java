package com.shuttle.ProfileChangeRequest;

import com.shuttle.credentials.dto.Credentials;
import com.shuttle.note.Note;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ProfileChangeRequest {
    private String name;
    private String surname;
    private String profilePicture;
    private String telephoneNumber;
    @OneToOne
    private Credentials credentials;
    private String address;
}
