package com.caue.bookstore.dto;

import jakarta.validation.constraints.NotBlank;

public class PasswordResetConfirm {
    @NotBlank(message = "Reset token must not be blank.")
    private String token;
    @NotBlank(message = "New password must not be blank.")
    private String newPassword;

    public PasswordResetConfirm() {
    }

    public PasswordResetConfirm(String token, String newPassword) {
        this.token = token;
        this.newPassword = newPassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}

