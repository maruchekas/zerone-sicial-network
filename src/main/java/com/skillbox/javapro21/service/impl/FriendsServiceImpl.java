package com.skillbox.javapro21.service.impl;

import com.skillbox.javapro21.api.request.dialogs.DialogRequestForCreate;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.MessageOkContent;
import com.skillbox.javapro21.api.response.account.AuthData;
import com.skillbox.javapro21.api.response.friends.StatusContent;
import com.skillbox.javapro21.domain.FriendshipStatus;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.domain.enumeration.FriendshipStatusType;
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

import static com.skillbox.javapro21.domain.enumeration.FriendshipStatusType.*;

@Component
@RequiredArgsConstructor
public class FriendsServiceImpl implements FriendsService {
    private final PersonRepository personRepository;
    private final UtilsService utilsService;

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
        utilsService.createFriendship(dst, src, FriendshipStatusType.DECLINED);
        return utilsService.getMessageOkResponse();
    }

    @Override
    public DataResponse<MessageOkContent> editFriend(Long id, Principal principal) {
        Person src = utilsService.findPersonByEmail(principal.getName());
        Person dst = personRepository.findPersonById(id).orElseThrow();
        FriendshipStatus friendshipStatusDst = utilsService.getFriendshipStatus(src.getId(), dst.getId());
        if (friendshipStatusDst != null && (friendshipStatusDst.getFriendshipStatusType().equals(REQUEST)
                || friendshipStatusDst.getFriendshipStatusType().equals(SUBSCRIBED)) ) {
            utilsService.createFriendship(src, dst, FRIEND);
        } else {
            utilsService.createFriendship(dst, src, REQUEST);
        }
        return utilsService.getMessageOkResponse();
    }

    @Override
    public ListDataResponse<AuthData> requestFriends(String name, int offset, int itemPerPage, Principal principal) {
        Person person = utilsService.findPersonByEmail(principal.getName());
        Pageable pageable = PageRequest.of(offset, itemPerPage);
        Page<Person> personPage;
        if (name.equals("")) {
            personPage = personRepository.findAllRequest(person.getId(), pageable);
        } else {
            personPage = personRepository.findAllRequestByName(person.getId(), name, pageable);
        }
        return getListDataResponse(personPage, pageable);
    }

    @Override
    public ListDataResponse<AuthData> recommendationsFriends(int offset, int itemPerPage, Principal principal) {
        Person person = utilsService.findPersonByEmail(principal.getName());
        Pageable pageable = PageRequest.of(offset, itemPerPage);
        List<Long> idsFriends = personRepository.findAllPersonFriends(person.getId(), pageable).toList()
                .stream().map(Person::getId).toList();
        Page<Person> personPage = personRepository.findRecommendedFriendsByPerson(person.getId(), idsFriends, pageable);
        return getListDataResponse(personPage, pageable);
    }

    @Override
    public DataResponse<StatusContent> isFriend(DialogRequestForCreate users, Principal principal) {
        Person person = utilsService.findPersonByEmail(principal.getName());
        List<Long> idsFriends = personRepository.findAllFriendsByPersonId(person.getId());
        idsFriends.retainAll(users.getUsersIds());
        if (idsFriends.size() > 0) {
            return new DataResponse<StatusContent>()
                    .setError("")
                    .setTimestamp(utilsService.getTimestamp())
                    .setData(new StatusContent().setUserId(person.getId()).setStatus("FRIEND"));
        }
        return new DataResponse<StatusContent>()
                .setError("")
                .setTimestamp(utilsService.getTimestamp())
                .setData(new StatusContent().setUserId(person.getId()).setStatus("NO FRIEND"));
    }

    @Override
    public ListDataResponse<AuthData> blockedFriends(String name, int offset, int itemPerPage, Principal principal) {
        Person person = utilsService.findPersonByEmail(principal.getName());
        Pageable pageable = PageRequest.of(offset, itemPerPage);
        Page<Person> personPage;
        if (name.equals("")) {
            personPage = personRepository.findAllBlockedPersons(person.getId(), pageable);
        } else {
            personPage = personRepository.findAllBlockedPersonsByName(person.getId(), name, pageable);
        }
        return getListDataResponse(personPage, pageable);
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
