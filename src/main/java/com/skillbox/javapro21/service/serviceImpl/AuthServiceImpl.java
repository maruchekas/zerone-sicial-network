package com.skillbox.javapro21.service.serviceImpl;

import com.skillbox.javapro21.api.request.AuthRequest;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.account.AccountContent;
import com.skillbox.javapro21.api.response.account.AuthContent;
import com.skillbox.javapro21.config.security.JwtGenerator;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.exception.NotSuchUserOrWrongPasswordException;
import com.skillbox.javapro21.repository.PersonRepository;
import com.skillbox.javapro21.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class AuthServiceImpl implements AuthService {

    private final PersonRepository personRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtGenerator jwtGenerator;

    @Override
    public DataResponse<AuthContent> login(AuthRequest authRequest) throws NotSuchUserOrWrongPasswordException {
        Person authPerson = findPersonByEmail(authRequest.getEmail());
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
        System.out.println(passwordEncoder.encode(authRequest.getEmail()));
        System.out.println(authPerson.getPassword());

            String email = authRequest.getEmail();
            String password = authRequest.getPassword();
            Person person = findPersonByEmail(email);
            String token = jwtGenerator.generateToken(email);
            System.out.println(person.getPassword());

            return getAuthResponse(person, token);


    }

    @Override
    public void logout() {
        if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated())
        SecurityContextHolder.clearContext();
    }

    private Person findPersonByEmail(String email) {
        return personRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
    }

    private Person findPersonByEmailAndPassword(String email, String password) {
        return personRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new UsernameNotFoundException(email));
    }

    private DataResponse<AuthContent> getAuthResponse(Person person, String token) {
        DataResponse<AuthContent> dataResponse = new DataResponse<>();
        dataResponse.setTimestamp(LocalDateTime.now());
        AuthContent authData = new AuthContent(person, token);
        dataResponse.setData(authData);
        return dataResponse;
    }
}
