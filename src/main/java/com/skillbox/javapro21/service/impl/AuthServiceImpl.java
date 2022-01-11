package com.skillbox.javapro21.service.impl;

import com.skillbox.javapro21.api.request.auth.AuthRequest;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.MessageOkContent;
import com.skillbox.javapro21.api.response.account.AuthData;
import com.skillbox.javapro21.config.security.JwtGenerator;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.exception.NotSuchUserOrWrongPasswordException;
import com.skillbox.javapro21.exception.UserLegalException;
import com.skillbox.javapro21.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UtilsService utilsService;
    private final JwtGenerator jwtGenerator;

    public DataResponse<AuthData> login(AuthRequest authRequest) throws NotSuchUserOrWrongPasswordException, UserLegalException {
        Person person = utilsService.findPersonByEmail(authRequest.getEmail());
        if (!isPersonLegal(person)) throw new UserLegalException("User with email: " + authRequest.getEmail() + " is blocked.");

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
        if (!passwordEncoder.matches(authRequest.getPassword(), person.getPassword()))
            throw new NotSuchUserOrWrongPasswordException("Неверный логин или пароль");

        String token = jwtGenerator.generateToken(authRequest.getEmail());
        return getSuccessAuthResponse(person, token);
    }

    public DataResponse<MessageOkContent> logout() {
        SecurityContextHolder.clearContext();
        return utilsService.getMessageOkResponse();
    }

    private boolean isPersonLegal(Person person) {
        boolean isApproved = person.getIsApproved().equals(1);
        boolean isBlocked = !person.getIsBlocked().equals(0);
        return isApproved && !isBlocked;
    }

    private DataResponse<AuthData> getSuccessAuthResponse(Person person, String token) {
        DataResponse<AuthData> dataResponse = new DataResponse<>();
        dataResponse.setError("ok");
        dataResponse.setTimestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());
        AuthData authData = utilsService.getAuthData(person, token);
        dataResponse.setData(authData);
        return dataResponse;
    }
}
