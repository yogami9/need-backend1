package com.needbackend_app.needapp.user.util;

public class PasswordValidation {

    public static void validatePasswords(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match");
        }
    }
}
