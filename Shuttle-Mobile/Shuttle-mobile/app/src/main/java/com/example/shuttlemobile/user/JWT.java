package com.example.shuttlemobile.user;

import com.example.shuttlemobile.driver.Driver;

import java.util.ArrayList;
import java.util.List;

public class JWT {
    private String iss;
    private String sub;
    private String aud;
    private Long iat;
    private Long exp;
    private Long id;
    private List<JWTRole> role;

    public String getEmail() {
        return sub;
    }

    public Long getId() {
        return id;
    }

    public List<JWTRole> getRolesRaw() {
        return role;
    }

    public List<User.Role> getRoles() {
        List<User.Role> r = new ArrayList<>();
        for (JWTRole jwtrole : getRolesRaw()) {
            if (jwtrole.getName().equals("passenger")) {
                r.add(User.Role.Passenger);
            } else if (jwtrole.getName().equals("driver")) {
                r.add(User.Role.Driver);
            }
        }
        return r;
    }

    public JWT() {

    }
}
