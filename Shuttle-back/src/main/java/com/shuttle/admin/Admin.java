package com.shuttle.admin;

import com.shuttle.credentials.Credentials;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    private String name;
    private String surname;
    private String profilePicture;
    boolean active;
    @OneToOne
    private Credentials credentials;
}
