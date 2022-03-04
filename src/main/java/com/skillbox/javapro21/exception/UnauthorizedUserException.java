package com.skillbox.javapro21.exception;

import static com.skillbox.javapro21.config.Constants.UNAUTHORISED_USER_ERR;

public class UnauthorizedUserException extends CustomException {
    public UnauthorizedUserException() {
        super(UNAUTHORISED_USER_ERR);
    }
}
