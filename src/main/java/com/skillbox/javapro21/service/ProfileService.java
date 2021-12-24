package com.skillbox.javapro21.service;

import com.skillbox.javapro21.api.response.DataResponse;
import org.springframework.stereotype.Service;

import java.security.Principal;
@Service
public interface ProfileService {
    DataResponse deletePerson(Principal principal);
}
