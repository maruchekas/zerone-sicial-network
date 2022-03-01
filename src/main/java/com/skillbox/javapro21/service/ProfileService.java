package com.skillbox.javapro21.service;

import com.skillbox.javapro21.api.request.post.PostRequest;
import com.skillbox.javapro21.api.request.profile.EditProfileRequest;
import com.skillbox.javapro21.api.response.Content;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.MessageOkContent;
import com.skillbox.javapro21.api.response.account.AuthData;
import com.skillbox.javapro21.api.response.post.PostData;
import com.skillbox.javapro21.exception.*;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.security.Principal;

@Service
public interface ProfileService {
    DataResponse<AuthData> getPerson(Principal principal);

    DataResponse<AuthData> editPerson(Principal principal, EditProfileRequest editProfileRequest)
            throws UserPrincipalNotFoundException;

    DataResponse<MessageOkContent> deletePerson(Principal principal);

    DataResponse<AuthData> getPersonById(Long id, Principal principal) throws PersonNotFoundException, InterlockedFriendshipStatusException;

    ListDataResponse<PostData> getPersonWallById(Long id, int offset, int itemPerPage, Principal principal)
            throws PersonNotFoundException, InterlockedFriendshipStatusException;

    DataResponse<PostData> postPostOnPersonWallById(Long id, Long publishDate, PostRequest postRequest, Principal principal)
            throws InterlockedFriendshipStatusException, PersonNotFoundException, PostNotFoundException;

    DataResponse<MessageOkContent> blockPersonById(Long id, Principal principal)
            throws InterlockedFriendshipStatusException, PersonNotFoundException, FriendshipNotFoundException;

    DataResponse<MessageOkContent> unblockPersonById(Long id, Principal principal)
            throws PersonNotFoundException, BlockPersonHimselfException, NonBlockedFriendshipException,
            InterlockedFriendshipStatusException, FriendshipNotFoundException;

    ListDataResponse<Content> searchByPerson(String firstName, String lastName, Integer ageFrom,
                                             Integer ageTo, String country, String city, Integer offset,
                                             Integer itemPerPage, Principal principal);
}
