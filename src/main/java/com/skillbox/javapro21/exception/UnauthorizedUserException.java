package com.skillbox.javapro21.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class UnauthorizedUserException extends CustomException {
    public UnauthorizedUserException(String message) {
        super(message);
    }
}
