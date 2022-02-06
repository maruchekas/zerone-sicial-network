package com.skillbox.javapro21.service;

import com.skillbox.javapro21.api.request.dialogs.DialogRequestForCreate;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.MessageOkContent;
import com.skillbox.javapro21.api.response.account.AuthData;
import com.skillbox.javapro21.api.response.friends.StatusContent;
import com.skillbox.javapro21.exception.FriendshipNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public interface FriendsService {
    ListDataResponse<AuthData> getFriends(String name, int offset, int itemPerPage, Principal principal);

    DataResponse<MessageOkContent> deleteFriend(Long id, Principal principal);

    DataResponse<MessageOkContent> revokeRequest(Long id, Principal principal);

    DataResponse<MessageOkContent> deleteSubscription(Long id, Principal principal) throws FriendshipNotFoundException;

    DataResponse<MessageOkContent> editFriend(Long id, Principal principal);

    ListDataResponse<AuthData> getIncomingRequests(String name, int offset, int itemPerPage, Principal principal);

    ListDataResponse<AuthData> getBlockedUsers(String name, int offset, int itemPerPage, Principal principal);

    ListDataResponse<AuthData> getSubscribers(String name, int offset, int itemPerPage, Principal principal);

    ListDataResponse<AuthData> getSubscriptions(String name, int offset, int itemPerPage, Principal principal);

    ListDataResponse<AuthData> getOutgoingRequests(String name, int offset, int itemPerPage, Principal principal);

    ListDataResponse<AuthData> recommendationsFriends(int offset, int itemPerPage, Principal principal);

    DataResponse<StatusContent> isFriend(DialogRequestForCreate users, Principal principal);
}
