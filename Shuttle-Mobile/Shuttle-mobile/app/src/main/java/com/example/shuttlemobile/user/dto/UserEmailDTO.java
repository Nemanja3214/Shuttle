package com.example.shuttlemobile.user.dto;

import java.io.Serializable;

public class UserEmailDTO implements Serializable {
    private Long id;
    private String email;

    public UserEmailDTO(Long id, String email) {
        this.id = id;
        this.email = email;
    }

    public UserEmailDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "UserEmailDTO{" +
                "id=" + id +
                ", email='" + email + '\'' +
                '}';
    }
}
