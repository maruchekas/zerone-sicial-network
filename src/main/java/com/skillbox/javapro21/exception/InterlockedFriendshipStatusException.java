package com.skillbox.javapro21.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static com.skillbox.javapro21.config.Constants.USER_WASBLOCKED_ERR;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class InterlockedFriendshipStatusException extends CustomException {
    public InterlockedFriendshipStatusException() {
        super(USER_WASBLOCKED_ERR);
    }
}
