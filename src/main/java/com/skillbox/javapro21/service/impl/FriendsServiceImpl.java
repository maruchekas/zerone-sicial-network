package com.skillbox.javapro21.service.impl;

import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.MessageOkContent;
import com.skillbox.javapro21.api.response.account.AuthData;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.domain.enumeration.FriendshipStatusType;
import com.skillbox.javapro21.repository.FriendshipRepository;
import com.skillbox.javapro21.repository.FriendshipStatusRepository;
import com.skillbox.javapro21.repository.PersonRepository;
import com.skillbox.javapro21.service.FriendsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FriendsServiceImpl implements FriendsService {
    private final PersonRepository personRepository;
    private final UtilsService utilsService;
    private final FriendshipRepository friendshipRepository;
    private final FriendshipStatusRepository friendshipStatusRepository;

    @Override
    public ListDataResponse<AuthData> getFriends(String name, int offset, int itemPerPage, Principal principal) {
        Person person = utilsService.findPersonByEmail(principal.getName());
        Pageable pageable = PageRequest.of(offset, itemPerPage);
        Page<Person> personsPage;
        if (name.trim().equals("")) {
            personsPage = personRepository.findAllPersonFriends(person.getId(), pageable);
        } else {
            personsPage = personRepository.findAllPersonFriendsAndName(person.getId(), name, pageable);
        }
        return getListDataResponse(personsPage, pageable);
    }

    @Override
    public DataResponse<MessageOkContent> deleteFriend(Long id, Principal principal) {
        Person src = utilsService.findPersonByEmail(principal.getName());
        Person dst = personRepository.findPersonById(id).orElseThrow();
        utilsService.createFriendship(src, dst, FriendshipStatusType.DECLINED);
        utilsService.createFriendship(dst, src, FriendshipStatusType.SUBSCRIBED);
        return utilsService.getMessageOkResponse();
    }

    @Override
    public DataResponse<MessageOkContent> editFriend(Long id, Principal principal) {
        Person src = utilsService.findPersonByEmail(principal.getName());
        Person dst = personRepository.findPersonById(id).orElseThrow();
        utilsService.createFriendship(src, dst, FriendshipStatusType.REQUEST);
        return utilsService.getMessageOkResponse();
    }

    private ListDataResponse<AuthData> getListDataResponse(Page<Person> personsPage, Pageable pageable) {
        ListDataResponse<AuthData> commentsDataListDataResponse = new ListDataResponse<>();
        commentsDataListDataResponse
                .setPerPage(pageable.getPageSize())
                .setTimestamp(utilsService.getTimestamp())
                .setOffset((int) pageable.getOffset())
                .setTotal(personsPage.getTotalPages())
                .setData(getAuthData(personsPage.toList()));
        return commentsDataListDataResponse;
    }

    private List<AuthData> getAuthData(List<Person> person) {
        List<AuthData> listAuthData = new ArrayList<>();
        person.forEach(p -> {
            AuthData authData = utilsService.getAuthData(p, null);
            listAuthData.add(authData);
        });
        return listAuthData;
    }
}
