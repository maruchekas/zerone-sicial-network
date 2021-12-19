package com.skillbox.javapro21.service;

import com.skillbox.javapro21.api.request.*;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.account.AccountContent;
import com.skillbox.javapro21.api.response.account.AuthContent;
import com.skillbox.javapro21.exception.NotSuchUserOrWrongPasswordException;
import com.skillbox.javapro21.exception.TokenConfirmationException;
import com.skillbox.javapro21.exception.UserExistException;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public interface AccountService {
    DataResponse<AccountContent> registration(RegisterRequest registerRequest) throws UserExistException;

    String verifyRegistration(String email, String code) throws TokenConfirmationException;

    String recovery(RecoveryRequest recoveryRequest);

    String verifyRecovery(String email, String code) throws TokenConfirmationException;

    String recoveryPassword(String email, String password);

    DataResponse<AccountContent> changePassword(ChangePasswordRequest changePasswordRequest);

    DataResponse<AccountContent> changeEmail(ChangeEmailRequest changeEmailRequest, Principal principal);

    DataResponse<AccountContent> changeNotifications(ChangeNotificationsRequest changeNotificationsRequest, Principal principal);
}
