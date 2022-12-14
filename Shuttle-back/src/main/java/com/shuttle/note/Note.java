package com.shuttle.note;

import com.shuttle.user.GenericUser;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String message;
    private String title;
    private LocalDateTime timeCreated;
    @OneToOne
    private GenericUser user;
}
