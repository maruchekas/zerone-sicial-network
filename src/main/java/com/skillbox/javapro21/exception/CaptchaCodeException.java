package com.skillbox.javapro21.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class CaptchaCodeException extends CustomException {
    public CaptchaCodeException(String message) {
        super(message);
    }
}
