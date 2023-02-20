package com.example.shuttlemobile.user.dto;

import java.io.Serializable;

public class PasswordResetDTO implements Serializable {
    private String newPassword;
    private String code;

    public PasswordResetDTO(String new_password, String code) {
        this.newPassword = new_password;
        this.code = code;
    }

    public PasswordResetDTO() {
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "PasswordResetDTO{" +
                "new_password='" + newPassword + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
