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
import com.skillbox.javapro21.domain.UserNotificationSettings;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.domain.enumeration.MessagesPermission;
import com.skillbox.javapro21.domain.enumeration.NotificationType;
import com.skillbox.javapro21.domain.enumeration.UserType;
import com.skillbox.javapro21.exception.CaptchaCodeException;
import com.skillbox.javapro21.exception.NotFoundException;
import com.skillbox.javapro21.exception.TokenConfirmationException;
import com.skillbox.javapro21.exception.UserExistException;
import com.skillbox.javapro21.repository.CaptchaRepository;
import com.skillbox.javapro21.repository.UserNotificationSettingsRepository;
import com.skillbox.javapro21.repository.PersonRepository;
import com.skillbox.javapro21.service.AccountService;
import com.skillbox.javapro21.service.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
import java.util.Optional;

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
    private final UserNotificationSettingsRepository userNotificationSettingsRepository;
    private final ResourceService resourceService;

    private static int countRegisterPost = 0;
    @Value(value = "${base_url}")
    private static String baseUrl;

    @Override
    public DataResponse<MessageOkContent> registration(RegisterRequest registerRequest)
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
        return utilsService.getMessageOkResponse();
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "persons", allEntries = true)
    })
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
        String text = confirmationUrl.getBaseUrl() + RECOVERY_URL + recoveryRequest.getEmail() + "&code=" + token;
        confirmPersonAndSendEmail(recoveryRequest.getEmail(), text, token);
        return MESSAGE_SENT_SUCCESS;
    }

    @Override
    public MessageOkContent verifyRecovery(String email, String code) throws TokenConfirmationException {
        Person person = utilsService.findPersonByEmail(email);
        if (person.getConfirmationCode().equals(code)) {
            if (person.getIsBlocked() == 2) {
                person.setIsBlocked(0);
                personRepository.save(person);
                return new MessageOkContent();
            }
            person
                    .setIsApproved(1)
                    .setUserType(UserType.USER)
                    .setMessagesPermission(MessagesPermission.ALL)
                    .setConfirmationCode("");
            personRepository.save(person);
            return new MessageOkContent();
        } else throw new TokenConfirmationException(CONFIRMATION_CODE_ERR);
    }

    @Override
    public String recoveryPassword(String email, String password) {
        Person person = utilsService.findPersonByEmail(email);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
        person.setPassword(passwordEncoder.encode(password));
        personRepository.save(person);
        return PASSWORD_CHANGE_SUCCESS;
    }

    @Override
    public DataResponse<MessageOkContent> changePassword(ChangePasswordRequest changePasswordRequest, Principal principal) throws IOException, MailjetException {
        Person person = utilsService.findPersonByEmail(principal.getName());
        String token = utilsService.getToken();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
        person
                .setPassword(passwordEncoder.encode(changePasswordRequest.getPassword()))
                .setIsApproved(0)
                .setMessagesPermission(MessagesPermission.NOBODY);
        personRepository.save(person);
        String text = confirmationUrl.getBaseUrl() + RECOVERY_URL + person.getEmail() + "&code=" + token;
        confirmPersonAndSendEmail(person.getEmail(), text, token);
        return utilsService.getMessageOkResponse();
    }

    @Override
    public DataResponse<MessageOkContent> changeEmail(ChangeEmailRequest changeEmailRequest, Principal principal) throws UserExistException, IOException, MailjetException {
        Optional<Person> byEmail = personRepository.findByEmail(changeEmailRequest.getEmail());
        String token = utilsService.getToken();
        if (byEmail.isEmpty()) {
            Person person = utilsService.findPersonByEmail(principal.getName());
            person.setEmail(changeEmailRequest.getEmail().toLowerCase(Locale.ROOT))
                    .setIsApproved(0)
                    .setMessagesPermission(MessagesPermission.NOBODY);
            personRepository.save(person);
            String text = confirmationUrl.getBaseUrl() + RECOVERY_URL + changeEmailRequest.getEmail() + "&code=" + token;
            confirmPersonAndSendEmail(changeEmailRequest.getEmail(), text, token);
            return utilsService.getMessageOkResponse();
        } else {
            throw new UserExistException("Пользователь с данным email уже существует");
        }
    }

    @Override
    @CacheEvict(value = "notifications", key = "#principal.name", allEntries = true)
    public DataResponse<MessageOkContent> changeNotifications(ChangeNotificationsRequest changeNotificationsRequest, Principal principal) throws NotFoundException {
        Person person = utilsService.findPersonByEmail(principal.getName());
        UserNotificationSettings userNotificationSettings = userNotificationSettingsRepository.findNotificationSettingsByPersonId(person.getId())
                .orElseThrow(NotFoundException::new);
        switch (changeNotificationsRequest.getNotificationType()) {
            case POST -> userNotificationSettings.setPost(changeNotificationsRequest.isEnable());
            case POST_COMMENT -> userNotificationSettings.setPostComment(changeNotificationsRequest.isEnable());
            case COMMENT_COMMENT -> userNotificationSettings.setCommentComment(changeNotificationsRequest.isEnable());
            case FRIEND_REQUEST -> userNotificationSettings.setFriendsRequest(changeNotificationsRequest.isEnable());
            case MESSAGE -> userNotificationSettings.setMessage(changeNotificationsRequest.isEnable());
            case FRIEND_BIRTHDAY -> userNotificationSettings.setFriendsBirthday(changeNotificationsRequest.isEnable());
        }
        userNotificationSettingsRepository.save(userNotificationSettings);
        return utilsService.getMessageOkResponse();
    }

    @Override
    @Cacheable(value = "notifications", key = "#principal.name")
    public ListDataResponse<NotificationSettingData> getNotifications(Principal principal) {
        log.info("caching notifications for " + principal.getName());
        Person person = utilsService.findPersonByEmail(principal.getName());
        UserNotificationSettings userNotificationSettings = userNotificationSettingsRepository.findNotificationSettingsByPersonId(person.getId())
                .orElse(new UserNotificationSettings()
                        .setPost(true)
                        .setPostComment(true)
                        .setCommentComment(true)
                        .setFriendsRequest(true)
                        .setMessage(true)
                        .setFriendsBirthday(true));
        UserNotificationSettings save = userNotificationSettingsRepository.save(userNotificationSettings);
        ListDataResponse<NotificationSettingData> dataResponse = new ListDataResponse<>();
        dataResponse.setTimestamp(utilsService.getTimestamp());
        dataListNotification(save);
        dataResponse.setData(dataListNotification(save));
        return dataResponse;
    }

    @Override
    public DataResponse<MessageOkContent> recoveryProfile(String email) throws IOException, MailjetException, UserExistException {
        Person person = utilsService.findPersonByEmail(email);
        if (person.getIsBlocked() == 2) {
            String token = utilsService.getToken();
            String text = confirmationUrl.getBaseUrl() + RECOVERY_URL + person.getEmail() + "&code=" + token;
            confirmPersonAndSendEmail(person.getEmail(), text, token);
        }
        throw new UserExistException(USER_EXISTS_ERR);
    }

    /**
     * Заполнение новыми парраметрами настроек оповещения
     */
    private List<NotificationSettingData> dataListNotification(UserNotificationSettings userNotificationSettings) {
        List<NotificationSettingData> list = new ArrayList<>();
        list.add(new NotificationSettingData().setNotificationType(NotificationType.POST)
                .setEnable(userNotificationSettings.isPost()));
        list.add(new NotificationSettingData().setNotificationType(NotificationType.POST_COMMENT)
                .setEnable(userNotificationSettings.isPostComment()));
        list.add(new NotificationSettingData().setNotificationType(NotificationType.COMMENT_COMMENT)
                .setEnable(userNotificationSettings.isCommentComment()));
        list.add(new NotificationSettingData().setNotificationType(NotificationType.FRIEND_REQUEST)
                .setEnable(userNotificationSettings.isFriendsRequest()));
        list.add(new NotificationSettingData().setNotificationType(NotificationType.MESSAGE)
                .setEnable(userNotificationSettings.isMessage()));
        list.add(new NotificationSettingData().setNotificationType(NotificationType.FRIEND_BIRTHDAY)
                .setEnable(userNotificationSettings.isFriendsBirthday()));
        return list;
    }

    private void mailMessageForRegistration(RegisterRequest registerRequest) throws MailjetException, IOException {
        String token = registerRequest.getCaptchaSecret();
        String text = confirmationUrl.getBaseUrl() + COMPLETE_REGISTER_URL + registerRequest.getEmail() + "&code=" + token;
        confirmPersonAndSendEmail(registerRequest.getEmail(), text, token);
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
        UserNotificationSettings userNotificationSettings = new UserNotificationSettings()
                .setPerson(person)
                .setPost(true)
                .setPostComment(true)
                .setCommentComment(true)
                .setFriendsRequest(true)
                .setMessage(true);
        userNotificationSettingsRepository.save(userNotificationSettings);
    }
}
