package com.example.shuttlemobile.common;

import com.example.shuttlemobile.user.User;

import java.io.Serializable;

public class SessionContext implements Serializable {
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
