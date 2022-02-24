package com.skillbox.javapro21.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static com.skillbox.javapro21.config.Constants.UNAUTHORISED_USER_ERR;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class UnauthorizedUserException extends CustomException {
    public UnauthorizedUserException() {
        super(UNAUTHORISED_USER_ERR);
    }
}
