package com.shuttle.note;

import com.shuttle.user.GenericUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String message;
    //private String title; <--- TODO: What's the point of this field?
    private LocalDateTime timeCreated;
    @ManyToOne
    private GenericUser user;
    @ManyToOne
    private GenericUser by;
}
