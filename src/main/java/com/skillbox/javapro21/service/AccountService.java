package com.skillbox.javapro21.service;

import com.mailjet.client.errors.MailjetException;
import com.skillbox.javapro21.api.request.account.*;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.MessageOkContent;
import com.skillbox.javapro21.api.response.account.NotificationSettingData;
import com.skillbox.javapro21.exception.CaptchaCodeException;
import com.skillbox.javapro21.exception.NotFoundException;
import com.skillbox.javapro21.exception.TokenConfirmationException;
import com.skillbox.javapro21.exception.UserExistException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.security.Principal;

@Service
public interface AccountService {
    DataResponse<MessageOkContent> registration(RegisterRequest registerRequest) throws UserExistException, MailjetException, IOException, CaptchaCodeException;

    ModelAndView verifyRegistration(String email, String code) throws TokenConfirmationException;

    String recoveryPasswordMessage(RecoveryRequest recoveryRequest) throws MailjetException, IOException;

    MessageOkContent verifyRecovery(String email, String code) throws TokenConfirmationException;

    String recoveryPassword(String email, String password);

    DataResponse<MessageOkContent> changePassword(ChangePasswordRequest changePasswordRequest, Principal principal) throws UserExistException, IOException, MailjetException;

    DataResponse<MessageOkContent> changeEmail(ChangeEmailRequest changeEmailRequest, Principal principal) throws UserExistException, IOException, MailjetException;

    DataResponse<MessageOkContent> changeNotifications(ChangeNotificationsRequest changeNotificationsRequest, Principal principal) throws NotFoundException;

    ListDataResponse<NotificationSettingData> getNotifications(Principal principal);

    DataResponse<MessageOkContent> recoveryProfile(String email) throws IOException, MailjetException, UserExistException;
}
