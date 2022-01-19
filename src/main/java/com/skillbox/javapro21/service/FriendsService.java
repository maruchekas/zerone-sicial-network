package com.skillbox.javapro21.service;

import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.MessageOkContent;
import com.skillbox.javapro21.api.response.account.AuthData;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public interface FriendsService {
    ListDataResponse<AuthData> getFriends(String name, int offset, int itemPerPage, Principal principal);

    DataResponse<MessageOkContent> deleteFriend(Long id, Principal principal);

    DataResponse<MessageOkContent> editFriend(Long id, Principal principal);

    ListDataResponse<AuthData> requestFriends(String name, int offset, int itemPerPage, Principal principal);
}
