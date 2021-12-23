package com.skillbox.javapro21.service;

import com.skillbox.javapro21.api.request.AuthRequest;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.account.AccountContent;
import com.skillbox.javapro21.api.response.account.AuthContent;
import com.skillbox.javapro21.exception.NotSuchUserOrWrongPasswordException;
import com.skillbox.javapro21.exception.SessionTerminationErrorException;

public interface AuthService {

    DataResponse<?> login(AuthRequest authRequest) throws NotSuchUserOrWrongPasswordException;

    DataResponse<?> logout();
}
