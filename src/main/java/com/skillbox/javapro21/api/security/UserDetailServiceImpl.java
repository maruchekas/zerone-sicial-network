package com.skillbox.javapro21.api.security;

import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.repository.PersonRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;


@Service
public class UserDetailServiceImpl implements UserDetailsService {
    private final PersonRepository personRepository;

    public UserDetailServiceImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email){
        Optional<Person> person = personRepository.findByEmail(email);
        person.ifPresent(p -> {
            p.setLastOnlineTime(Instant.now());
            personRepository.save(p);
        });
        return (UserDetails) person.orElse(null);
    }
}
