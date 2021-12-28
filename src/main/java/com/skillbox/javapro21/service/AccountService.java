package com.skillbox.javapro21.service;

import com.mailjet.client.errors.MailjetException;
import com.skillbox.javapro21.api.request.account.*;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.MessageOkContent;
import com.skillbox.javapro21.api.response.account.NotificationSettingData;
import com.skillbox.javapro21.exception.TokenConfirmationException;
import com.skillbox.javapro21.exception.UserExistException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public interface AccountService {
    DataResponse<MessageOkContent> registration(RegisterRequest registerRequest) throws UserExistException, MailjetException;

    String verifyRegistration(String email, String code) throws TokenConfirmationException;

    String recoveryPasswordMessage(RecoveryRequest recoveryRequest) throws MailjetException;

    String verifyRecovery(String email, String code) throws TokenConfirmationException;

    String recoveryPassword(String email, String password);

    DataResponse<MessageOkContent> changePassword(ChangePasswordRequest changePasswordRequest);

    DataResponse<MessageOkContent> changeEmail(ChangeEmailRequest changeEmailRequest, Principal principal);

    DataResponse<MessageOkContent> changeNotifications(ChangeNotificationsRequest changeNotificationsRequest, Principal principal);

    ListDataResponse<NotificationSettingData> getNotifications(Principal principal);
}
