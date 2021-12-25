package com.skillbox.javapro21.service.serviceImpl;

import com.skillbox.javapro21.api.request.auth.AuthRequest;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.MessageOkContent;
import com.skillbox.javapro21.api.response.account.AuthContent;
import com.skillbox.javapro21.config.security.JwtGenerator;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.exception.NotSuchUserOrWrongPasswordException;
import com.skillbox.javapro21.exception.UserLegalException;
import com.skillbox.javapro21.repository.PersonRepository;
import com.skillbox.javapro21.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Component
public class AuthServiceImpl extends AbstractMethodClass implements AuthService {

    private final PersonRepository personRepository;
    private final JwtGenerator jwtGenerator;

    protected AuthServiceImpl(PersonRepository personRepository, JwtGenerator jwtGenerator) {
        super(personRepository);
        this.personRepository = personRepository;
        this.jwtGenerator = jwtGenerator;
    }

    public DataResponse<AuthContent> login(AuthRequest authRequest) throws NotSuchUserOrWrongPasswordException, UserLegalException {
        Person person = findPersonByEmail(authRequest.getEmail());
        if (!isPersonLegal(person)) throw new UserLegalException(authRequest.getEmail());

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
        if (passwordEncoder.matches(authRequest.getPassword(), person.getPassword()))
            throw new NotSuchUserOrWrongPasswordException();

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

    private DataResponse<AuthContent> getSuccessAuthResponse(Person person, String token) {
        DataResponse<AuthContent> dataResponse = new DataResponse<>();
        dataResponse.setError("ok");
        dataResponse.setTimestamp(LocalDateTime.now());
        AuthContent authData = getAuthData(person, token);
        dataResponse.setData(authData);
        return dataResponse;
    }

    private AuthContent getAuthData(Person person, String token) {
        AuthContent authContent = new AuthContent()
                .setId(person.getId())
                .setFirstName(person.getFirstName())
                .setLastName(person.getLastName())
                .setRegDate(Timestamp.valueOf(person.getRegDate()))
                .setEmail(person.getEmail())
                .setMessagePermission(person.getMessagesPermission())
                .setLastOnlineTime(Timestamp.valueOf(person.getLastOnlineTime()))
                .setIsBlocked(isBlockedPerson(person))
                .setToken(token);
        if (person.getPhone() != null) authContent.setPhone(person.getPhone());
        if (person.getPhoto() != null) authContent.setPhoto(person.getPhoto());
        if (person.getAbout() != null) authContent.setAbout(person.getAbout());
        if (person.getCountry() != null) {
            authContent.setCity(Map.of("id", person.getId().toString(), "City", person.getTown()));
            authContent.setCountry(Map.of("id", person.getId().toString(), "Country", person.getCountry()));
        }
        if (person.getBirthDate() != null) authContent.setBirthDate(Timestamp.valueOf(person.getBirthDate()));
        return authContent;
    }
}
