package com.skillbox.javapro21.service.serviceImpl;

import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;
import com.skillbox.javapro21.api.request.account.*;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.account.AccountContent;
import com.skillbox.javapro21.api.response.account.NotificationSettingData;
import com.skillbox.javapro21.config.MailjetSender;
import com.skillbox.javapro21.api.response.account.AuthContent;
import com.skillbox.javapro21.config.properties.ConfirmationRecoveryPass;
import com.skillbox.javapro21.config.properties.ConfirmationRegistration;
import com.skillbox.javapro21.config.security.JwtGenerator;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.domain.enumeration.MessagesPermission;
import com.skillbox.javapro21.domain.enumeration.UserType;
import com.skillbox.javapro21.exception.NotSuchUserOrWrongPasswordException;
import com.skillbox.javapro21.exception.TokenConfirmationException;
import com.skillbox.javapro21.exception.UserExistException;
import com.skillbox.javapro21.repository.NotificationTypeRepository;
import com.skillbox.javapro21.repository.PersonRepository;
import com.skillbox.javapro21.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Component
public class AccountServiceImpl implements AccountService {
    private final PersonRepository personRepository;
    private final MailjetSender mailMessage;
    private final ConfirmationRegistration confirmationRegistration;
    private final ConfirmationRecoveryPass confirmationRecoveryPass;
    private final JwtGenerator jwtGenerator;
    private final NotificationTypeRepository notificationTypeRepository;

    @Autowired
    public AccountServiceImpl(PersonRepository personRepository, MailjetSender mailMessage, ConfirmationRegistration confirmationRegistration, ConfirmationRecoveryPass confirmationRecoveryPass, JwtGenerator jwtGenerator, NotificationTypeRepository notificationTypeRepository) {
        this.personRepository = personRepository;
        this.mailMessage = mailMessage;
        this.confirmationRegistration = confirmationRegistration;
        this.confirmationRecoveryPass = confirmationRecoveryPass;
        this.jwtGenerator = jwtGenerator;
        this.notificationTypeRepository = notificationTypeRepository;
    }

    //Todo: нужна ли проверка каптчи?
    public DataResponse<AccountContent> registration(RegisterRequest registerRequest) throws UserExistException, MailjetSocketTimeoutException, MailjetException {
        if (personRepository.findByEmail(registerRequest.getEmail()).isPresent()) throw new UserExistException();
        createNewPerson(registerRequest);
        mailMessageForRegistration(registerRequest);
        return getAccountResponse();
    }

    public String verifyRegistration(String email, String code) throws TokenConfirmationException {
        Person person = findPersonByEmail(email);
        if (person.getConfirmationCode().equals(code)) {
            person.setIsApproved(1);
            person.setUserType(UserType.USER);
            person.setMessagesPermission(MessagesPermission.ALL);
            person.setConfirmationCode("");
            personRepository.save(person);
        } else throw new TokenConfirmationException();
        return "Пользователь подтвержден";
    }

    public String recoveryPasswordMessage(RecoveryRequest recoveryRequest) throws MailjetSocketTimeoutException, MailjetException {
        String token = getToken();
        String text  = confirmationRecoveryPass.getUrl() + "?email=" + recoveryRequest.getEmail() + "&code=" + token;
        confirmPersonAndSendEmail(recoveryRequest.getEmail(), text, token);
        return "Ссылка отправлена на почту";
    }

    private void mailMessageForRegistration(RegisterRequest registerRequest) throws MailjetSocketTimeoutException, MailjetException {
        String token = getToken();
        String text = confirmationRegistration.getUrl() + "?email=" + registerRequest.getEmail() + "&code=" + token;
        confirmPersonAndSendEmail(registerRequest.getEmail(), text, token);
    }

    public String verifyRecovery(String email, String code) throws TokenConfirmationException {
        Person person = findPersonByEmail(email);
        if (person.getConfirmationCode().equals(code)) {
            return "Пользователь может приступить к изменению пароля";
        } else throw new TokenConfirmationException();
    }

    public String recoveryPassword(String email, String password) {
        Person person = findPersonByEmail(email);
        person.setPassword(password);
        personRepository.save(person);
        return "Пароль успешно изменен";
    }

    public DataResponse<AccountContent> changePassword(ChangePasswordRequest changePasswordRequest) {
        Person person = findPersonByEmail(jwtGenerator.getLoginFromToken(changePasswordRequest.getToken()));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
        person.setPassword(passwordEncoder.encode(changePasswordRequest.getPassword()));
        personRepository.save(person);
        return getAccountResponse();
    }

    public DataResponse<AccountContent> changeEmail(ChangeEmailRequest changeEmailRequest, Principal principal) {
        Person person = findPersonByEmail(principal.getName());
        person.setEmail(changeEmailRequest.getEmail());
        return getAccountResponse();
    }

    //Todo: сделать как будет поправлена бд
    public DataResponse<AccountContent> changeNotifications(ChangeNotificationsRequest changeNotificationsRequest, Principal principal) {
        Person person = findPersonByEmail(principal.getName());
        return null;
    }

    //Todo: сделать как будет поправлена бд
    public ListDataResponse<NotificationSettingData> getNotifications(Principal principal) {
        return null;
    }

    private void confirmPersonAndSendEmail(String email, String text, String token) throws MailjetSocketTimeoutException, MailjetException {
        Person person = findPersonByEmail(email);
        person.setConfirmationCode(token);
        personRepository.save(person);

        mailMessage.send(email,text);
    }

    private void createNewPerson(RegisterRequest registerRequest) {
        Person person = new Person();
        person.setEmail(registerRequest.getEmail());
        person.setFirstName(registerRequest.getFirstName());
        person.setLastName(registerRequest.getLastName());
        person.setConfirmationCode(registerRequest.getCode());
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
        person.setPassword(passwordEncoder.encode(registerRequest.getPasswd1()));
        person.setRegDate(LocalDateTime.now());
        person.setLastOnlineTime(LocalDateTime.now());
        personRepository.save(person);
    }

    /**
     * поиск пользователя по почте, если не найден выбрасывает ошибку
     */
    private Person findPersonByEmail(String email) {
        return personRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
    }

    /**
     * создание рандомного токена
     */
    private String getToken() {
        return new Random().ints(10, 33, 122)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    /**
     * используется для ответа 200
     */
    private DataResponse<AccountContent> getAccountResponse() {
        DataResponse<AccountContent> dataResponse = new DataResponse<>();
        dataResponse.setTimestamp(LocalDateTime.now());
        AccountContent accountData = new AccountContent();
        Map<String, String> data = new HashMap<>();
        data.put("message", "ok");
        accountData.setData(data);
        dataResponse.setData(accountData);
        return dataResponse;
    }
}
