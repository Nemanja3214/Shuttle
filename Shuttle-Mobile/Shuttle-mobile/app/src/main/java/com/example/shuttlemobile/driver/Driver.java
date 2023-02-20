package com.example.shuttlemobile.driver;

import com.example.shuttlemobile.user.User;

import java.time.LocalTime;

public class Driver extends User {
    private boolean isAvailable;
    private boolean blocked;
    private LocalTime workedToday;

    public Driver() {
        this.setName("Driver");
        this.setLastName("Driverson");
    }
}
