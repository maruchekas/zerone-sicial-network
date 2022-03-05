package com.skillbox.javapro21.exception;

import static com.skillbox.javapro21.config.Constants.USER_INTERLOCKED_ERR;

public class InterlockedFriendshipStatusException extends CustomException {
    public InterlockedFriendshipStatusException() {
        super(USER_INTERLOCKED_ERR);
    }
}
