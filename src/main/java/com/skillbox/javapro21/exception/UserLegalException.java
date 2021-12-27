package com.skillbox.javapro21.exception;

public class UserLegalException extends Exception{
    public UserLegalException(String email) {
        System.out.println("User with email: " + email + " is blocked.");
    }
}
