package com.skillbox.javapro21.service.serviceImpl;

import com.skillbox.javapro21.api.request.RegisterRequest;
import com.skillbox.javapro21.api.response.AccountResponse;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.domain.enumeration.MessagesPermission;
import com.skillbox.javapro21.domain.enumeration.UserType;
import com.skillbox.javapro21.exception.UserExistException;
import com.skillbox.javapro21.repository.PersonRepository;
import com.skillbox.javapro21.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class AccountServiceImpl implements AccountService {
    private final PersonRepository personRepository;

    @Autowired
    public AccountServiceImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public AccountResponse registration(RegisterRequest registerRequest) throws UserExistException {
        if (personRepository.findByEmail(registerRequest.getEmail()).isPresent()) throw new UserExistException();
        createNewPerson(registerRequest);
        return getAccountResponse();
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
        person.setMessagesPermission(MessagesPermission.All);
        person.setUserType(UserType.USER);
        personRepository.save(person);
    }

    private Person findPersonByEmail(String email) {
        return personRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
    }

    /**
     * используется для ответа 200
     */
    private AccountResponse getAccountResponse() {
        AccountResponse accountResponse = new AccountResponse();
        accountResponse.setTimestamp(Instant.from(LocalDateTime.now()));
        Map<String, String> data = new HashMap<>();
        data.put("message", "ok");
        accountResponse.setData(data);
        return accountResponse;
    }
}
