package com.skillbox.javapro21.service.serviceImpl;

import com.skillbox.javapro21.api.request.RecoveryRequest;
import com.skillbox.javapro21.api.request.RegisterRequest;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.account.AccountData;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.domain.enumeration.MessagesPermission;
import com.skillbox.javapro21.domain.enumeration.UserType;
import com.skillbox.javapro21.exception.UserExistException;
import com.skillbox.javapro21.repository.PersonRepository;
import com.skillbox.javapro21.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Component
public class AccountServiceImpl implements AccountService {
    private final PersonRepository personRepository;
    private final JavaMailSender mailSender;

    @Value("$(spring.mail.verificationLink)")
    private String verificationLink;

    @Autowired
    public AccountServiceImpl(PersonRepository personRepository, JavaMailSender mailSender) {
        this.personRepository = personRepository;
        this.mailSender = mailSender;
    }

    //Todo: добавить проверку каптчи
    //Todo: добавить отправку сообщения с ссылкой по почте
    public DataResponse registration(RegisterRequest registerRequest) throws UserExistException {
        if (personRepository.findByEmail(registerRequest.getEmail()).isPresent()) throw new UserExistException();
        createNewPerson(registerRequest);
        mailMessageForRegistration(registerRequest);
        return getAccountResponse();
    }

    public String verifyRegistration(String email, String code) {
        Person person = findPersonByEmail(email);
        if (person.getConfirmationCode().equals(code)) {
            person.setIsApproved(1);
            person.setUserType(UserType.USER);
            person.setMessagesPermission(MessagesPermission.All);
            person.setConfirmationCode("");
            personRepository.save(person);
        }
        return "Пользователь подтвержден";
    }

    public String recovery(RecoveryRequest recoveryRequest) {
        Person person = findPersonByEmail(recoveryRequest.getEmail());
        String token = getToken();
        person.setConfirmationCode(token);
        personRepository.save(person);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(recoveryRequest.getEmail());
        mailMessage.setSubject("Код для восстановления пароля");
        mailMessage.setText("Введите этот код: " + token);

        mailSender.send(mailMessage);
        return "Код выслан на почту";
    }

    //Todo: перенести добавление роли в контроллер верификации по почте
    private void createNewPerson(RegisterRequest registerRequest) {
        Person person = new Person();
        person.setEmail(registerRequest.getEmail());
        person.setFirstName(registerRequest.getFirstName());
        person.setLastName(registerRequest.getLastName());
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
        mailMessage.setText(verificationLink + "?email=" + registerRequest.getEmail() + "&code=" + token);

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
    private DataResponse<AccountData> getAccountResponse() {
        DataResponse<AccountData> dataResponse = new DataResponse<AccountData>();
        dataResponse.setTimestamp(Instant.from(LocalDateTime.now()));
        AccountData accountData = new AccountData();
        Map<String, String> data = new HashMap<>();
        data.put("message", "ok");
        accountData.setData(data);
        dataResponse.setData(accountData);
        return dataResponse;
    }
}
