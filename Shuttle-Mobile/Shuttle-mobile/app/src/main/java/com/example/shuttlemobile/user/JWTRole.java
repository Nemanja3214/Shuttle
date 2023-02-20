package com.example.shuttlemobile.user;

public class JWTRole {
    private String name;

    public JWTRole() {
    }

    public JWTRole(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "JWTRole{" +
                "name='" + name + '\'' +
                '}';
    }
}
