package com.shuttle.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import org.springframework.security.core.GrantedAuthority;

import java.io.Serial;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Role implements GrantedAuthority {


    @Serial
    private static final long serialVersionUID = 7015991771388810177L;
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name="name")
    String name;

    @JsonIgnore
    @Override
    public String getAuthority() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @JsonIgnore
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
