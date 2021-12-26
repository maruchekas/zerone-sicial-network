package com.skillbox.javapro21.aop;

import com.skillbox.javapro21.repository.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@Aspect
@Component
public class AddedLastOnlineTimeForPersonWithAOP {
    private final PersonRepository personRepository;

    private static String email;

    @Autowired
    public AddedLastOnlineTimeForPersonWithAOP(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

//    @Pointcut("execution(public * com.skillbox.javapro21.service.serviceImpl.ProfileServiceImpl.*(..))")
//    public void callAtServiceProfile() {
//    }
//
//    @Before("callAtServiceProfile()")
//    public void getEmailBeforeDelete() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        email = auth.getName();
//    }
//
//    @AfterReturning("callAtServiceProfile()")
//    public void setLastOnlineTime() {
//        Optional<Person> person = personRepository.findByEmail(email);
//        person.get().setLastOnlineTime(LocalDateTime.now());
//        log.info("Пользователь {} совершил действие {} воспользовавшись методом из класса ProfileService.", person.get().getEmail(), LocalDateTime.now());
//        personRepository.save(person.get());
//    }
}
