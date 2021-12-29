package com.skillbox.javapro21.service.impl;

import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.MessageOkContent;
import com.skillbox.javapro21.api.response.account.AuthData;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.Random;

@Component
public class UtilsService {
    private final PersonRepository personRepository;

    @Autowired
    protected UtilsService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    /**
     * поиск пользователя по почте, если не найден выбрасывает ошибку
     */
    public Person findPersonByEmail(String email) {
        return personRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
    }

    /**
     * используется для ответа 200 "message: ok"
     */
    public DataResponse<MessageOkContent> getMessageOkResponse() {
        DataResponse<MessageOkContent> dataResponse = new DataResponse<>();
        dataResponse.setTimestamp(LocalDateTime.now());
        MessageOkContent accountData = new MessageOkContent();
        accountData.setMessage("ok");
        dataResponse.setData(accountData);
        return dataResponse;
    }

    /**
     * создание рандомного токена
     */
    public String getToken() {
        return new Random().ints(10, 33, 122)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }


    /**
     * заблокирован пользователь или нет ?
     */
    public String isBlockedPerson(Person person){
        return person.getIsBlocked() == 0 ? "false" : "true";
    }

    /**
     * заполнение данных о пользователе
     */
    public AuthData getAuthData(Person person, String token) {
        AuthData authData = new AuthData()
                .setId(person.getId())
                .setFirstName(person.getFirstName())
                .setLastName(person.getLastName())
                .setRegDate(Timestamp.valueOf(person.getRegDate()))
                .setEmail(person.getEmail())
                .setMessagePermission(person.getMessagesPermission())
                .setLastOnlineTime(Timestamp.valueOf(person.getLastOnlineTime()))
                .setIsBlocked(isBlockedPerson(person))
                .setToken(token);
        if (person.getPhone() != null) authData.setPhone(person.getPhone());
        if (person.getPhoto() != null) authData.setPhoto(person.getPhoto());
        if (person.getAbout() != null) authData.setAbout(person.getAbout());
        if (person.getCountry() != null) {
            authData.setCity(Map.of("id", person.getId().toString(), "City", person.getTown()));
            authData.setCountry(Map.of("id", person.getId().toString(), "Country", person.getCountry()));
        }
        if (person.getBirthDate() != null) authData.setBirthDate(Timestamp.valueOf(person.getBirthDate()));

        return authData;
    }

    /**
     * получение LocalDateTime из TimestampAccessor, который отдает фронт
     */
    public LocalDateTime getLocalDateTime(long dateWithTimestampAccessor) {
        return LocalDateTime.parse(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        .format(new Date (dateWithTimestampAccessor)),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
