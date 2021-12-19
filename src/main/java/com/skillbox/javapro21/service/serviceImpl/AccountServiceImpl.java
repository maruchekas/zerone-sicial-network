package com.skillbox.javapro21.service.serviceImpl;

import com.skillbox.javapro21.api.request.account.*;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.account.AccountContent;
import com.skillbox.javapro21.api.response.account.NotificationSettingData;
import com.skillbox.javapro21.config.properties.ConfirmationRecoveryPass;
import com.skillbox.javapro21.config.properties.ConfirmationRegistration;
import com.skillbox.javapro21.config.security.JwtGenerator;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.domain.enumeration.MessagesPermission;
import com.skillbox.javapro21.domain.enumeration.UserType;
import com.skillbox.javapro21.exception.TokenConfirmationException;
import com.skillbox.javapro21.exception.UserExistException;
import com.skillbox.javapro21.repository.NotificationTypeRepository;
import com.skillbox.javapro21.repository.PersonRepository;
import com.skillbox.javapro21.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
    private final JavaMailSender mailSender;
    private final ConfirmationRegistration confirmationRegistration;
    private final ConfirmationRecoveryPass confirmationRecoveryPass;
    private final JwtGenerator jwtGenerator;
    private final NotificationTypeRepository notificationTypeRepository;

    @Autowired
    public AccountServiceImpl(PersonRepository personRepository, JavaMailSender mailSender, ConfirmationRegistration confirmationRegistration, ConfirmationRecoveryPass confirmationRecoveryPass, JwtGenerator jwtGenerator, NotificationTypeRepository notificationTypeRepository) {
        this.personRepository = personRepository;
        this.mailSender = mailSender;
        this.confirmationRegistration = confirmationRegistration;
        this.confirmationRecoveryPass = confirmationRecoveryPass;
        this.jwtGenerator = jwtGenerator;
        this.notificationTypeRepository = notificationTypeRepository;
    }

    //Todo: нужна ли проверка каптчи?
    public DataResponse<AccountContent> registration(RegisterRequest registerRequest) throws UserExistException {
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
            person.setMessagesPermission(MessagesPermission.All);
            person.setConfirmationCode("");
            personRepository.save(person);
        } else throw new TokenConfirmationException();
        return "Пользователь подтвержден";
    }

    public String recoveryPasswordMessage(RecoveryRequest recoveryRequest) {
        Person person = findPersonByEmail(recoveryRequest.getEmail());
        String token = getToken();
        person.setConfirmationCode(token);
        personRepository.save(person);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(recoveryRequest.getEmail());
        mailMessage.setSubject("Ссылка на страницу восстановления пароля");
        mailMessage.setText(confirmationRecoveryPass.getUrl() + "?email=" + recoveryRequest.getEmail() + "&code=" + token);

        mailSender.send(mailMessage);
        return "Ссылка отправлена на почту";
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

    public DataResponse<AccountContent> changeNotifications(ChangeNotificationsRequest changeNotificationsRequest, Principal principal) {
        Person person = findPersonByEmail(principal.getName());
//        NotificationType notificationType = notificationTypeRepository.findByPersonId(person.getId());
        return null;
    }

    @Override
    public ListDataResponse<NotificationSettingData> getNotifications(Principal principal) {
        return null;
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

    private void mailMessageForRegistration(RegisterRequest registerRequest) {
        Person person = findPersonByEmail(registerRequest.getEmail());
        String token = getToken();
        person.setConfirmationCode(token);
        personRepository.save(person);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(registerRequest.getEmail());
        mailMessage.setSubject("Подтвердите регистрацию в социальной сети Zerone!");
        mailMessage.setText(confirmationRegistration.getUrl() + "?email=" + registerRequest.getEmail() + "&code=" + token);

        mailSender.send(mailMessage);
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
        dataResponse.setTimestamp(Instant.from(LocalDateTime.now()));
        AccountContent accountData = new AccountContent();
        Map<String, String> data = new HashMap<>();
        data.put("message", "ok");
        accountData.setData(data);
        dataResponse.setData(accountData);
        return dataResponse;
    }
}
