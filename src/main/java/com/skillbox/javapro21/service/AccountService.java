package com.skillbox.javapro21.service;

import com.skillbox.javapro21.api.request.RecoveryRequest;
import com.skillbox.javapro21.api.request.RegisterRequest;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.exception.TokenConfirmationException;
import com.skillbox.javapro21.exception.UserExistException;
import org.springframework.stereotype.Service;

@Service
public interface AccountService {
    DataResponse registration(RegisterRequest registerRequest) throws UserExistException;

    String verifyRegistration(String email, String code) throws TokenConfirmationException;

    String recovery(RecoveryRequest recoveryRequest);

    String verifyRecovery(String email, String code) throws TokenConfirmationException;
}
