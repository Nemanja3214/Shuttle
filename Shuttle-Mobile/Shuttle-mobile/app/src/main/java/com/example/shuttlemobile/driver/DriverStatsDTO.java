package com.example.shuttlemobile.driver;

public class DriverStatsDTO {
    double rejections;
    double rides;
    double hoursWorked;
    double moneyEarned;

    public DriverStatsDTO() {
    }

    public DriverStatsDTO(double rejections, double rides, double hoursWorked, double moneyEarned) {
        this.rejections = rejections;
        this.rides = rides;
        this.hoursWorked = hoursWorked;
        this.moneyEarned = moneyEarned;
    }

    public void setRejections(double rejections) {
        this.rejections = rejections;
    }

    public void setRides(double rides) {
        this.rides = rides;
    }

    public void setHoursWorked(double hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    public void setMoneyEarned(double moneyEarned) {
        this.moneyEarned = moneyEarned;
    }

    public double getRejections() {
        return rejections;
    }

    public double getRides() {
        return rides;
    }

    public double getHoursWorked() {
        return hoursWorked;
    }

    public double getMoneyEarned() {
        return moneyEarned;
    }
}
