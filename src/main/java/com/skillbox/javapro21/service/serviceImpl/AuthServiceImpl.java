package com.skillbox.javapro21.service.serviceImpl;

import com.skillbox.javapro21.api.request.AuthRequest;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.account.AuthContent;
import com.skillbox.javapro21.config.security.JwtGenerator;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.exception.NotSuchUserOrWrongPasswordException;
import com.skillbox.javapro21.repository.PersonRepository;
import com.skillbox.javapro21.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class AuthServiceImpl implements AuthService {

    private final PersonRepository personRepository;
    private final JwtGenerator jwtGenerator;

    @Override
    public DataResponse<AuthContent> login(AuthRequest authRequest) throws NotSuchUserOrWrongPasswordException {
        Person person = findPersonByEmail(authRequest.getEmail());
        String email = authRequest.getEmail();
        if (person != null && isPersonLegal(person)) {
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
            if (passwordEncoder.matches(authRequest.getPassword(), person.getPassword())) {
                String token = jwtGenerator.generateToken(email);
                return getSuccessAuthResponse(person, token);
            }
        }
        return getFailedAuthResponse();
    }

    @Override
    public DataResponse<AuthContent> logout() {

        if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            SecurityContextHolder.clearContext();
            DataResponse<AuthContent> dataResponse = new DataResponse<>();
            dataResponse.setError("ok");
            dataResponse.setTimestamp(LocalDateTime.now());
            return dataResponse;
        } else
            return getFailedAuthResponse();
    }

    private Person findPersonByEmail(String email) {
        return personRepository.findByEmail(email).orElse(null);
    }

    private boolean isPersonLegal(Person person){
        boolean isApproved = person.getIsApproved().equals(1);
        boolean isBlocked = !person.getIsBlocked().equals(0);
        return isApproved && !isBlocked;
    }

    private DataResponse<AuthContent> getSuccessAuthResponse(Person person, String token) {
        DataResponse<AuthContent> dataResponse = new DataResponse<>();
        dataResponse.setError("ok");
        dataResponse.setTimestamp(LocalDateTime.now());
        AuthContent authData = new AuthContent(person, token);
        dataResponse.setData(authData);
        return dataResponse;
    }

    private DataResponse<AuthContent> getFailedAuthResponse() {
        DataResponse<AuthContent> dataResponse = new DataResponse<>();
        dataResponse.setError("Invalid request");
        dataResponse.setErrorDescription("Incorrect email/password combination");
        return dataResponse;
    }
}
