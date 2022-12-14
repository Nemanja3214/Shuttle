package com.shuttle.user;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

public class UserActivation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    private GenericUser user;
    private LocalDateTime time;
    // lifetime??
}
