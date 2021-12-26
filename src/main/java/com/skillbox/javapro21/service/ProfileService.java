package com.skillbox.javapro21.service;

import com.skillbox.javapro21.api.request.profile.EditProfileRequest;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.domain.Person;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public interface ProfileService {
    DataResponse<Person> getPerson(Principal principal);

    DataResponse<Person> editPerson(Principal principal, EditProfileRequest editProfileRequest);

    DataResponse deletePerson(Principal principal);
}
