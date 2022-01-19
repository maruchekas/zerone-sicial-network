package com.skillbox.javapro21.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class PostLikeNotFoundException extends Exception {
    public PostLikeNotFoundException(String message) {
        super(message);
    }
}
