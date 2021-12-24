package com.skillbox.javapro21.service.serviceImpl;

import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.repository.PersonRepository;
import com.skillbox.javapro21.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.time.LocalDateTime;

@Component
public class ProfileServiceImpl extends AbstractMethodClass implements ProfileService {
    private final PersonRepository personRepository;

    @Autowired
    protected ProfileServiceImpl(PersonRepository personRepository) {
        super(personRepository);
        this.personRepository = personRepository;
    }

    public DataResponse deletePerson(Principal principal) {
        Person person = findPersonByEmail(principal.getName());
        person.setIsBlocked(2);
        person.setLastOnlineTime(LocalDateTime.now());
        personRepository.save(person);
        SecurityContextHolder.clearContext();
        return getAccountResponse();
    }
}
