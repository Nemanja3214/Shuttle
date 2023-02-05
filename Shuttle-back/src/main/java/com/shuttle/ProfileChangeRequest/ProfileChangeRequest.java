package com.shuttle.ProfileChangeRequest;


import com.shuttle.driver.Driver;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class ProfileChangeRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    private String name;
    private String surname;
    @Column(name = "profile_picture", length = 10000)
    private String profilePicture;
    private String telephoneNumber;
    @OneToOne(fetch = FetchType.EAGER)
    private Driver user;
    private String address;
}
