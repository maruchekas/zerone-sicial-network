package com.skillbox.javapro21.aop;

import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AddedLastOnlineTimeForPersonWithAOP {
    private final PersonRepository personRepository;

    @Around("@annotation(LastActivity)")
    public Object setLastActivity(ProceedingJoinPoint joinPoint) throws Throwable {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Object proceed = joinPoint.proceed();
        if (email.matches("^(.+)@(.+)$")) {
            Person person = personRepository.findByEmail(email).orElseThrow();
            person.setLastOnlineTime(LocalDateTime.now());
            personRepository.save(person);
            log.info(email + "; " + "LastActivity: " + personRepository.findByEmail(email).get().getLastOnlineTime() + "; Класс контроллера: " + joinPoint.getTarget().getClass());
        }
        return proceed;
    }
}
