package com.skillbox.javapro21.service;

import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.MessageOkContent;
import com.skillbox.javapro21.api.request.profile.EditProfileRequest;
import com.skillbox.javapro21.api.response.account.AuthData;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public interface ProfileService {
    DataResponse<AuthData> getPerson(Principal principal);

    DataResponse<AuthData> editPerson(Principal principal, EditProfileRequest editProfileRequest);

    DataResponse<MessageOkContent> deletePerson(Principal principal);
}
