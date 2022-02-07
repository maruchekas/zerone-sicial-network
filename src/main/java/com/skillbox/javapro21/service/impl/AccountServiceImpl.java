package com.skillbox.javapro21.service.impl;

import com.mailjet.client.errors.MailjetException;
import com.skillbox.javapro21.api.request.account.*;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.MessageOkContent;
import com.skillbox.javapro21.api.response.account.NotificationSettingData;
import com.skillbox.javapro21.config.MailjetSender;
import com.skillbox.javapro21.config.properties.ConfirmationUrl;
import com.skillbox.javapro21.config.security.JwtGenerator;
import com.skillbox.javapro21.domain.NotificationType;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.domain.enumeration.MessagesPermission;
import com.skillbox.javapro21.domain.enumeration.NotificationTypeStatus;
import com.skillbox.javapro21.domain.enumeration.UserType;
import com.skillbox.javapro21.exception.CaptchaCodeException;
import com.skillbox.javapro21.exception.TokenConfirmationException;
import com.skillbox.javapro21.exception.UserExistException;
import com.skillbox.javapro21.repository.CaptchaRepository;
import com.skillbox.javapro21.repository.NotificationTypeRepository;
import com.skillbox.javapro21.repository.PersonRepository;
import com.skillbox.javapro21.service.AccountService;
import com.skillbox.javapro21.service.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.skillbox.javapro21.config.Constants.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final UtilsService utilsService;
    private final PersonRepository personRepository;
    private final CaptchaRepository captchaRepository;
    private final MailjetSender mailMessage;
    private final ConfirmationUrl confirmationUrl;
    private final JwtGenerator jwtGenerator;
    private final NotificationTypeRepository notificationTypeRepository;
    private final ResourceService resourceService;

    private static int countRegisterPost = 0;
    @Value(value = "${base_url}")
    private static String baseUrl;

    @Override
    public ResponseEntity<?> registration(RegisterRequest registerRequest)
            throws UserExistException, MailjetException, IOException, CaptchaCodeException {
        String captcha = registerRequest.getCaptcha();
        if (!captcha.equals(captchaRepository.findBySecretCode(registerRequest.getCaptchaSecret()).getCode())) {
            throw new CaptchaCodeException(CAPTCHA_CODE_ERR);
        }
        if (personRepository.findByEmail(registerRequest.getEmail().toLowerCase(Locale.ROOT)).isPresent()) {
            countRegisterPost = countRegisterPost + 1;
            Person personInBD = personRepository.findByEmail(registerRequest.getEmail().toLowerCase(Locale.ROOT)).orElseThrow();
            if (countRegisterPost < 3 && personInBD.getIsApproved() == 0) {
                updateNewPerson(personInBD, registerRequest);
                mailMessageForRegistration(registerRequest);
            } else {
                throw new UserExistException(USER_EXISTS_ERR);
            }
        } else {
            createNewPerson(registerRequest);
            mailMessageForRegistration(registerRequest);
        }
        return new ResponseEntity<>(utilsService.getMessageOkResponse(), HttpStatus.OK);
    }

    @Override
    public ModelAndView verifyRegistration(String email, String code) throws TokenConfirmationException {
        Person person = utilsService.findPersonByEmail(email);
        if (person.getConfirmationCode().equals(code)) {
            person
                    .setIsApproved(1)
                    .setUserType(UserType.USER)
                    .setMessagesPermission(MessagesPermission.ALL)
                    .setConfirmationCode("");

            try {
                person.setPhoto(resourceService.setDefaultAvatarToUser(email));
            } catch (IOException e) {
                person.setPhoto(null);
                e.printStackTrace();
            }

            personRepository.save(person);
        } else throw new TokenConfirmationException(CONFIRMATION_CODE_ERR);
        return new ModelAndView("redirect:" + baseUrl);
    }

    @Override
    public String recoveryPasswordMessage(RecoveryRequest recoveryRequest) throws MailjetException, IOException {
        String token = utilsService.getToken();
        String text = confirmationUrl.getBaseUrl() + RECOVERY_PASSWORD_URL + recoveryRequest.getEmail() + "&code=" + token;
        confirmPersonAndSendEmail(recoveryRequest.getEmail(), text, token);
        return MESSAGE_SENT_SUCCESS;
    }

    private void mailMessageForRegistration(RegisterRequest registerRequest) throws MailjetException, IOException {
        String token = registerRequest.getCaptchaSecret();
        String text = confirmationUrl.getBaseUrl() + COMPLETE_REGISTER_URL + registerRequest.getEmail() + "&code=" + token;
        confirmPersonAndSendEmail(registerRequest.getEmail(), text, token);
    }

    @Override
    public String verifyRecovery(String email, String code) throws TokenConfirmationException {
        Person person = utilsService.findPersonByEmail(email);
        if (person.getConfirmationCode().equals(code)) {
            return PASSWORD_CHANGE_ALLOW;
        } else throw new TokenConfirmationException(CONFIRMATION_CODE_ERR);
    }

    @Override
    public String recoveryPassword(String email, String password) {
        Person person = utilsService.findPersonByEmail(email);
        person.setPassword(password);
        personRepository.save(person);
        return PASSWORD_CHANGE_SUCCESS;
    }

    @Override
    public DataResponse<MessageOkContent> changePassword(ChangePasswordRequest changePasswordRequest) {
        Person person = utilsService.findPersonByEmail(jwtGenerator.getLoginFromToken(changePasswordRequest.getToken()));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
        person.setPassword(passwordEncoder.encode(changePasswordRequest.getPassword()));
        personRepository.save(person);
        return utilsService.getMessageOkResponse();
    }

    @Override
    public DataResponse<MessageOkContent> changeEmail(ChangeEmailRequest changeEmailRequest, Principal principal) {
        Person person = utilsService.findPersonByEmail(principal.getName());
        person.setEmail(changeEmailRequest.getEmail().toLowerCase(Locale.ROOT));
        return utilsService.getMessageOkResponse();
    }

    @Override
    public DataResponse<MessageOkContent> changeNotifications(ChangeNotificationsRequest changeNotificationsRequest, Principal principal) {
        Person person = utilsService.findPersonByEmail(principal.getName());
        NotificationType notificationType = notificationTypeRepository.findNotificationTypeByPersonId(person.getId())
                .orElse(new NotificationType()
                        .setPost(false)
                        .setPostComment(false)
                        .setCommentComment(false)
                        .setFriendsRequest(false)
                        .setMessage(false)
                        .setFriendsBirthday(false));
        switch (changeNotificationsRequest.getNotificationTypeStatus()) {
            case POST -> notificationType.setPost(changeNotificationsRequest.isEnable());
            case POST_COMMENT -> notificationType.setPostComment(changeNotificationsRequest.isEnable());
            case COMMENT_COMMENT -> notificationType.setCommentComment(changeNotificationsRequest.isEnable());
            case FRIEND_REQUEST -> notificationType.setFriendsRequest(changeNotificationsRequest.isEnable());
            case MESSAGE -> notificationType.setMessage(changeNotificationsRequest.isEnable());
            case FRIEND_BIRTHDAY -> notificationType.setFriendsBirthday(changeNotificationsRequest.isEnable());
        }
        notificationTypeRepository.save(notificationType);
        return utilsService.getMessageOkResponse();
    }

    @Override
    public ListDataResponse<NotificationSettingData> getNotifications(Principal principal) {
        Person person = utilsService.findPersonByEmail(principal.getName());
        NotificationType notificationType = notificationTypeRepository.findNotificationTypeByPersonId(person.getId())
                .orElse(new NotificationType()
                        .setPost(true)
                        .setPostComment(true)
                        .setCommentComment(true)
                        .setFriendsRequest(true)
                        .setMessage(true)
                        .setFriendsBirthday(true));
        ListDataResponse<NotificationSettingData> dataResponse = new ListDataResponse<>();
        dataResponse.setTimestamp(utilsService.getTimestamp());
        dataListNotification(notificationType);
        dataResponse.setData(dataListNotification(notificationType));
        return dataResponse;
    }

    /**
     * Заполнение новыми парраметрами настроек оповещения
     */
    private List<NotificationSettingData> dataListNotification(NotificationType notificationType) {
        List<NotificationSettingData> list = new ArrayList<>();
        list.add(new NotificationSettingData().setNotificationTypeStatus(NotificationTypeStatus.POST)
                .setEnable(notificationType.isPost()));
        list.add(new NotificationSettingData().setNotificationTypeStatus(NotificationTypeStatus.POST_COMMENT)
                .setEnable(notificationType.isPostComment()));
        list.add(new NotificationSettingData().setNotificationTypeStatus(NotificationTypeStatus.COMMENT_COMMENT)
                .setEnable(notificationType.isCommentComment()));
        list.add(new NotificationSettingData().setNotificationTypeStatus(NotificationTypeStatus.FRIEND_REQUEST)
                .setEnable(notificationType.isFriendsRequest()));
        list.add(new NotificationSettingData().setNotificationTypeStatus(NotificationTypeStatus.MESSAGE)
                .setEnable(notificationType.isMessage()));
        list.add(new NotificationSettingData().setNotificationTypeStatus(NotificationTypeStatus.FRIEND_BIRTHDAY)
                .setEnable(notificationType.isFriendsBirthday()));
        return list;
    }

    /**
     * Отправка на почту письма с токеном
     */
    private void confirmPersonAndSendEmail(String email, String text, String token) throws MailjetException, IOException {
        Person person = utilsService.findPersonByEmail(email);
        person.setConfirmationCode(token);
        personRepository.save(person);
        mailMessage.send(email, text);
    }

    /**
     * Создание пользователя без верификации
     */
    private void createNewPerson(RegisterRequest registerRequest) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

        Person person = new Person()
                .setEmail(registerRequest.getEmail().toLowerCase(Locale.ROOT))
                .setFirstName(registerRequest.getFirstName())
                .setLastName(registerRequest.getLastName())
                .setConfirmationCode(registerRequest.getCaptchaSecret())
                .setIsApproved(0)
                .setPassword(passwordEncoder.encode(registerRequest.getPasswd1()))
                .setRegDate(LocalDateTime.now(ZoneOffset.UTC))
                .setLastOnlineTime(LocalDateTime.now(ZoneOffset.UTC))
                .setIsBlocked(0)
                .setMessagesPermission(MessagesPermission.NOBODY);

        personRepository.save(person);
        globalNotificationsSettings(person);
    }

    /**
     * обновление пользователя без верификации
     */
    private void updateNewPerson(Person person, RegisterRequest registerRequest) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
        person
                .setEmail(registerRequest.getEmail().toLowerCase(Locale.ROOT))
                .setFirstName(registerRequest.getFirstName())
                .setLastName(registerRequest.getLastName())
                .setConfirmationCode(registerRequest.getCaptchaSecret())
                .setIsApproved(0)
                .setPassword(passwordEncoder.encode(registerRequest.getPasswd1()))
                .setRegDate(LocalDateTime.now(ZoneOffset.UTC))
                .setLastOnlineTime(LocalDateTime.now(ZoneOffset.UTC))
                .setIsBlocked(0)
                .setMessagesPermission(MessagesPermission.NOBODY);
        personRepository.save(person);
        globalNotificationsSettings(person);
    }

    /**
     * Стартовые настройки оповещения
     */
    private void globalNotificationsSettings(Person person) {
        NotificationType notificationType = new NotificationType()
                .setPerson(person)
                .setPost(true)
                .setPostComment(true)
                .setCommentComment(true)
                .setFriendsRequest(true)
                .setMessage(true);
        notificationTypeRepository.save(notificationType);
    }
}
