package com.skillbox.javapro21.aop;

import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.repository.PersonRepository;
import org.aspectj.lang.annotation.*;
import org.hibernate.annotations.common.util.impl.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;


@Aspect
@Component
public class MyAspect {
    private final PersonRepository personRepository;

    private static String email;

    @Autowired
    public MyAspect(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Pointcut("execution(public * com.skillbox.javapro21.service.serviceImpl.ProfileServiceImpl.*(..))")
    public void callAtMyServicePublic() { }

    @Before("callAtMyServicePublic()")
    public void getEmailBeforeDelete() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        email = auth.getName();
    }

    @AfterReturning("callAtMyServicePublic()")
    public void setLastOnlineTime() {
        Optional<Person> person = personRepository.findByEmail(email);
        person.get().setLastOnlineTime(LocalDateTime.now());
        personRepository.save(person.get());
    }
}
