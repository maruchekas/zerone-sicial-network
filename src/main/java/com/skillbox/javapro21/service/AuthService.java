package com.skillbox.javapro21.service;

import com.skillbox.javapro21.api.request.AuthRequest;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.account.AuthContent;
import com.skillbox.javapro21.exception.NotSuchUserOrWrongPasswordException;

public interface AuthService {

    DataResponse<AuthContent> login(AuthRequest authRequest) throws NotSuchUserOrWrongPasswordException;

    DataResponse<AuthContent> logout();
}
