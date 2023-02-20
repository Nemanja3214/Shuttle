package com.example.shuttlemobile.user.dto;

public class UserChatDataDTO {
    String email;
    String primaryRole;
    String pfp;

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPrimaryRole(String primaryRole) {
        this.primaryRole = primaryRole;
    }

    public void setPfp(String pfp) {
        this.pfp = pfp;
    }

    public String getEmail() {
        return email;
    }

    public String getPrimaryRole() {
        return primaryRole;
    }

    public String getPfp() {
        return pfp;
    }
}
