package com.skillbox.javapro21.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class CommentNotAuthorException extends Exception {
    public CommentNotAuthorException(String message) {
        super(message);
    }
}
