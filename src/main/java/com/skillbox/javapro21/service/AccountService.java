package com.skillbox.javapro21.service;

import com.skillbox.javapro21.api.request.RegisterRequest;
import com.skillbox.javapro21.api.response.AccountResponse;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.exception.UserExistException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public interface AccountService {
    AccountResponse registration(RegisterRequest registerRequest) throws UserExistException;
    Person findPersonByEmail(String email) throws UsernameNotFoundException;
}
