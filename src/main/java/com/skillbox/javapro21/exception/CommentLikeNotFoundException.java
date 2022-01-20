package com.skillbox.javapro21.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class CommentLikeNotFoundException extends CustomException {
    public CommentLikeNotFoundException(String message) {
        super(message);
    }
}
