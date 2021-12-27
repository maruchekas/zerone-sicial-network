package com.skillbox.javapro21.service;

import com.skillbox.javapro21.api.request.auth.AuthRequest;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.MessageOkContent;
import com.skillbox.javapro21.api.response.account.AuthData;
import com.skillbox.javapro21.exception.NotSuchUserOrWrongPasswordException;
import com.skillbox.javapro21.exception.UserLegalException;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {

    DataResponse<AuthData> login(AuthRequest authRequest) throws NotSuchUserOrWrongPasswordException, UserLegalException;

    DataResponse<MessageOkContent> logout();

}

