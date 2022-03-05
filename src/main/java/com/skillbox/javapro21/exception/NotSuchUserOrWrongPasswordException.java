package com.skillbox.javapro21.exception;

public class NotSuchUserOrWrongPasswordException extends CustomException {
    public NotSuchUserOrWrongPasswordException(String message) {
        super(message);
    }
}
