package com.skillbox.javapro21.service;

import com.skillbox.javapro21.api.request.post.PostRequest;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.MessageOkContent;
import com.skillbox.javapro21.api.request.profile.EditProfileRequest;
import com.skillbox.javapro21.api.response.account.AuthData;
import com.skillbox.javapro21.api.response.post.PostData;
import com.skillbox.javapro21.exception.PersonNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public interface ProfileService {
    DataResponse<AuthData> getPerson(Principal principal);

    DataResponse<AuthData> editPerson(Principal principal, EditProfileRequest editProfileRequest);

    DataResponse<MessageOkContent> deletePerson(Principal principal);

    DataResponse<AuthData> getPersonById(Long id) throws PersonNotFoundException;

    ListDataResponse<PostData> getPersonWallById(Long id, int offset, int itemPerPage);

    DataResponse<PostData> postPostOnPersonWallById(Long id, Long publishDate, PostRequest postRequest, Principal principal);
}
