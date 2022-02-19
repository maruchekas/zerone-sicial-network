package com.skillbox.javapro21.exception.exception_handling;

import com.skillbox.javapro21.api.response.BadDataResponse;
import com.skillbox.javapro21.exception.CaptchaCodeException;
import com.skillbox.javapro21.exception.UserExistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHAndler {

    @ExceptionHandler
    ResponseEntity<BadDataResponse> handleIncorrectCaptchaException(CaptchaCodeException exception) {
        BadDataResponse badDataResponse = new BadDataResponse().setError("captcha").setDescription(exception.getMessage());
        return new ResponseEntity<>(badDataResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    ResponseEntity<BadDataResponse> handleUserExistException(UserExistException exception) {
        BadDataResponse badDataResponse = new BadDataResponse().setError("email").setDescription(exception.getMessage());
        return new ResponseEntity<>(badDataResponse, HttpStatus.BAD_REQUEST);
    }

}
