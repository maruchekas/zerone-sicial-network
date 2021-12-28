package com.skillbox.javapro21.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AuthorAndUserEqualsException extends Exception{
    public AuthorAndUserEqualsException(String message) {
        super(message);
    }
}
