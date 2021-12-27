package com.skillbox.javapro21.service;

import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.MessageOkContent;
import com.skillbox.javapro21.api.request.profile.EditProfileRequest;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.profile.EditProfileResponse;
import com.skillbox.javapro21.domain.Person;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public interface ProfileService {
    DataResponse<EditProfileResponse> getPerson(Principal principal);

    DataResponse<EditProfileResponse> editPerson(Principal principal, EditProfileRequest editProfileRequest);

    DataResponse<MessageOkContent> deletePerson(Principal principal);
}
