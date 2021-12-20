package com.skillbox.javapro21.service.serviceImpl;

import com.skillbox.javapro21.api.request.AuthRequest;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.account.AccountContent;
import com.skillbox.javapro21.api.response.account.AuthContent;
import com.skillbox.javapro21.config.security.JwtGenerator;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.domain.enumeration.MessagesPermission;
import com.skillbox.javapro21.domain.enumeration.UserType;
import com.skillbox.javapro21.exception.NotSuchUserOrWrongPasswordException;
import com.skillbox.javapro21.repository.PersonRepository;
import com.skillbox.javapro21.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class AuthServiceImpl implements AuthService {

    private final PersonRepository personRepository;

    private final JwtGenerator jwtGenerator;

    @Override
    public DataResponse<AuthContent> login(AuthRequest authRequest) throws NotSuchUserOrWrongPasswordException {
        if (findPersonByEmail(authRequest.getEmail()) == null ||
                !findPersonByEmail(authRequest.getEmail()).getPassword().equals(authRequest.getPassword()))
            throw new NotSuchUserOrWrongPasswordException();
        Person person = findPersonByEmailAndPassword(authRequest.getEmail(), authRequest.getPassword());

            String token = jwtGenerator.generateToken(authRequest.getEmail());



        return getAuthResponse(person);
    }

    @Override
    public DataResponse<AuthContent> logout() {
        DataResponse<AuthContent> dataResponse = new DataResponse<>();
        dataResponse.setTimestamp(Instant.from(LocalDateTime.now()));
        SecurityContextHolder.clearContext();
        return dataResponse;
    }

    private Person findPersonByEmail(String email) {
        return personRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
    }

    private Person findPersonByEmailAndPassword(String email, String password) {
        return personRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new UsernameNotFoundException(email));
    }

    private DataResponse<AuthContent> getAuthResponse(Person person) {
        DataResponse<AuthContent> dataResponse = new DataResponse<>();
        dataResponse.setTimestamp(Instant.from(LocalDateTime.now()));
        AuthContent authData = new AuthContent();
        authData.setPerson(person);
        dataResponse.setData(authData);
        return dataResponse;
    }
}
