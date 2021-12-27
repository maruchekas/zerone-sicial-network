package com.skillbox.javapro21.service.serviceImpl;

import com.skillbox.javapro21.api.request.auth.AuthRequest;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.MessageOkContent;
import com.skillbox.javapro21.api.response.account.AuthData;
import com.skillbox.javapro21.config.security.JwtGenerator;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.exception.NotSuchUserOrWrongPasswordException;
import com.skillbox.javapro21.exception.UserLegalException;
import com.skillbox.javapro21.repository.PersonRepository;
import com.skillbox.javapro21.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class AuthServiceImpl extends AbstractMethodClass implements AuthService {
    private final PersonRepository personRepository;
    private final JwtGenerator jwtGenerator;

    @Autowired
    protected AuthServiceImpl(PersonRepository personRepository, JwtGenerator jwtGenerator) {
        super(personRepository);
        this.personRepository = personRepository;
        this.jwtGenerator = jwtGenerator;
    }

    public DataResponse<AuthData> login(AuthRequest authRequest) throws NotSuchUserOrWrongPasswordException, UserLegalException {
        Person person = findPersonByEmail(authRequest.getEmail());
        if (!isPersonLegal(person)) throw new UserLegalException("User with email: " + authRequest.getEmail() + " is blocked.");

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
        if (!passwordEncoder.matches(authRequest.getPassword(), person.getPassword()))
            throw new NotSuchUserOrWrongPasswordException("Неверный логин или пароль");

        String token = jwtGenerator.generateToken(authRequest.getEmail());
        return getSuccessAuthResponse(person, token);
    }

    public DataResponse<MessageOkContent> logout() {
        SecurityContextHolder.clearContext();
        return getMessageOkResponse();
    }

    private boolean isPersonLegal(Person person) {
        boolean isApproved = person.getIsApproved().equals(1);
        boolean isBlocked = !person.getIsBlocked().equals(0);
        return isApproved && !isBlocked;
    }

    private DataResponse<AuthData> getSuccessAuthResponse(Person person, String token) {
        DataResponse<AuthData> dataResponse = new DataResponse<>();
        dataResponse.setError("ok");
        dataResponse.setTimestamp(LocalDateTime.now());
        AuthData authData = getAuthData(person, token);
        dataResponse.setData(authData);
        return dataResponse;
    }
}
