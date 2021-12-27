package com.skillbox.javapro21.service.serviceImpl;

import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.MessageOkContent;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.Random;

public abstract class AbstractMethodClass {
    private final PersonRepository personRepository;

    @Autowired
    protected AbstractMethodClass(PersonRepository personRepository) {
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
     * заблокирован поьзователь или нет ?
     */
    public String isBlockedPerson(Person person){
        return person.getIsBlocked() == 0 ? "false" : "true";
    }

}
