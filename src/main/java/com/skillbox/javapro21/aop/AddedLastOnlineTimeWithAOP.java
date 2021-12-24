package com.skillbox.javapro21.aop;

import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.repository.PersonRepository;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;


@Aspect
@Component
public class AddedLastOnlineTimeWithAOP {
    private final PersonRepository personRepository;

    private static String email;

    @Autowired
    public AddedLastOnlineTimeWithAOP(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Pointcut("execution(public * com.skillbox.javapro21.service.serviceImpl.ProfileServiceImpl.*(..))")
    public void callAtMyServiceProfile() {

    }

    @Before("callAtMyServiceProfile()")
    public void getEmailBeforeDelete() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        email = auth.getName();
    }

    @AfterReturning("callAtMyServiceProfile()")
    public void setLastOnlineTime() {
        Optional<Person> person = personRepository.findByEmail(email);
        person.get().setLastOnlineTime(LocalDateTime.now());
        personRepository.save(person.get());
    }
}
