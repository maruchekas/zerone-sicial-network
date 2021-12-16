package com.skillbox.javapro21.service;

import com.skillbox.javapro21.api.request.RecoveryRequest;
import com.skillbox.javapro21.api.request.RegisterRequest;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.exception.UserExistException;
import org.springframework.stereotype.Service;

@Service
public interface AccountService {
    DataResponse registration(RegisterRequest registerRequest) throws UserExistException;
    DataResponse recovery(RecoveryRequest recoveryRequest);
}
