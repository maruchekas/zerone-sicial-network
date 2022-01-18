package com.skillbox.javapro21.service.impl;

import com.skillbox.javapro21.api.request.post.PostRequest;
import com.skillbox.javapro21.api.request.profile.EditProfileRequest;
import com.skillbox.javapro21.api.response.Content;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.MessageOkContent;
import com.skillbox.javapro21.api.response.account.AuthData;
import com.skillbox.javapro21.api.response.post.PostData;
import com.skillbox.javapro21.domain.Friendship;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.domain.Post;
import com.skillbox.javapro21.domain.enumeration.FriendshipStatusType;
import com.skillbox.javapro21.exception.*;
import com.skillbox.javapro21.repository.FriendshipRepository;
import com.skillbox.javapro21.repository.FriendshipStatusRepository;
import com.skillbox.javapro21.repository.PersonRepository;
import com.skillbox.javapro21.repository.PostRepository;
import com.skillbox.javapro21.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.skillbox.javapro21.domain.enumeration.FriendshipStatusType.*;

@Component
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final UtilsService utilsService;
    private final PersonRepository personRepository;
    private final PostRepository postRepository;
    private final PostServiceImpl postService;
    private final FriendshipRepository friendshipRepository;
    private final FriendshipStatusRepository friendshipStatusRepository;

    @Override
    public DataResponse<AuthData> getPerson(Principal principal) {
        Person person = utilsService.findPersonByEmail(principal.getName());
        return getPersonDataResponse(person);
    }

    @Override
    public DataResponse<AuthData> editPerson(Principal principal, EditProfileRequest editProfileRequest) {
        Person person = utilsService.findPersonByEmail(principal.getName());
        Person sPerson = savePersonByRequest(person, editProfileRequest);
        return getPersonDataResponse(sPerson);
    }

    @Override
    public DataResponse<MessageOkContent> deletePerson(Principal principal) {
        Person person = utilsService.findPersonByEmail(principal.getName())
                .setIsBlocked(2);
        personRepository.save(person);
        SecurityContextHolder.clearContext();
        return utilsService.getMessageOkResponse();
    }

    @Override
    public DataResponse<AuthData> getPersonById(Long id) throws PersonNotFoundException {
        Person person = personRepository.findPersonById(id).orElseThrow(() -> new PersonNotFoundException("Пользователя с данным айди не существует"));
        return getPersonDataResponse(person);
    }

    @Override
    public ListDataResponse<PostData> getPersonWallById(Long id, int offset, int itemPerPage, Principal principal) throws PersonNotFoundException, InterlockedFriendshipStatusException {
        Person src = utilsService.findPersonByEmail(principal.getName());
        Person dst = personRepository.findPersonById(id).orElseThrow(() -> new PersonNotFoundException("Пользователя с данным id не существует"));
        Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
        Optional<Friendship> optionalFriendship = friendshipRepository.findFriendshipBySrcPersonAndDstPerson(src.getId(), id);
        if (src.getId().equals(id)) {
            Page<Post> posts = postRepository.findPostsByAuthorId(id, pageable);
            return postService.getPostsResponse(offset, itemPerPage, posts);
        }
        if (utilsService.isBlockedBy(src.getId(), dst.getId(), optionalFriendship)) {
            Page<Post> posts = postRepository.findPostsByPersonId(id, pageable);
            return postService.getPostsResponse(offset, itemPerPage, posts);
        }
        throw new InterlockedFriendshipStatusException("Пользователь заблокирован и не может смотреть посты");
    }

    @Override
    public DataResponse<PostData> postPostOnPersonWallById(Long id, Long publishDate, PostRequest postRequest, Principal principal) throws InterlockedFriendshipStatusException, PersonNotFoundException, PostNotFoundException {
        Person src = utilsService.findPersonByEmail(principal.getName());
        Person dst = personRepository.findPersonById(id).orElseThrow(() -> new PersonNotFoundException("Пользователя с данным id не существует"));
        Post post;
        if (src.getId().equals(id)) {
            post = new Post()
                    .setTitle(postRequest.getTitle())
                    .setPostText(postRequest.getPostText())
                    .setIsBlocked(0)
                    .setAuthor(dst);
            if (publishDate != -1) {
                post.setTime(utilsService.getLocalDateTime(publishDate));
            } else {
                post.setTime(LocalDateTime.now(ZoneOffset.UTC));
            }
        } else {
            Optional<Friendship> optionalFriendship = friendshipRepository.findFriendshipBySrcPersonAndDstPerson(src.getId(), id);
            if (utilsService.isBlockedBy(src.getId(), dst.getId(), optionalFriendship)) {
                post = new Post()
                        .setTitle(postRequest.getTitle())
                        .setPostText(postRequest.getPostText())
                        .setTime(LocalDateTime.now(ZoneOffset.UTC))
                        .setIsBlocked(0)
                        .setAuthor(dst);
            } else throw new InterlockedFriendshipStatusException("Один из пользователей заблокирован для другого");
        }
        postRepository.save(post);
        return postService.getDataResponse(postService.getPostData(post));

    }

    @Override
    public DataResponse<MessageOkContent> blockPersonById(Long id, Principal principal) throws BlockPersonHimselfException, InterlockedFriendshipStatusException, PersonNotFoundException, FriendshipNotFoundException {
        Person src = utilsService.findPersonByEmail(principal.getName());
        Person dst = personRepository.findPersonById(id).orElseThrow(() -> new PersonNotFoundException("Пользователя с данным id не существует"));
        if (src.getId().equals(dst.getId()))
            throw new BlockPersonHimselfException("Пользователь пытается заблокировать сам себя");
        Optional<Friendship> optionalFriendship = friendshipRepository.findFriendshipBySrcPersonAndDstPerson(src.getId(), id);
        Friendship friendship = optionalFriendship.orElseThrow(FriendshipNotFoundException::new);
        if (utilsService.isBlockedBy(src.getId(), dst.getId(), optionalFriendship)) {
            if (!friendship.getFriendshipStatus().getFriendshipStatusType().equals(BLOCKED)) {
                utilsService.createFriendship(src, dst, BLOCKED);
            } else if (friendship.getFriendshipStatus().getFriendshipStatusType().equals(WASBLOCKED)) {
                utilsService.createFriendship(src, dst, FriendshipStatusType.WASBLOCKED);
            } else throw new InterlockedFriendshipStatusException("Уже взаимно заблокированы");
        }
        return utilsService.getMessageOkResponse();
    }

    @Override
    public DataResponse<MessageOkContent> unblockPersonById(Long id, Principal principal) throws PersonNotFoundException, BlockPersonHimselfException, NonBlockedFriendshipException, InterlockedFriendshipStatusException, FriendshipNotFoundException {
        Person src = utilsService.findPersonByEmail(principal.getName());
        Person dst = personRepository.findPersonById(id).orElseThrow(() -> new PersonNotFoundException("Пользователя с данным id не существует"));
        Optional<Friendship> optionalFriendship = friendshipRepository.findFriendshipBySrcPersonAndDstPerson(src.getId(), id);
        if (src.getId().equals(id)) throw new BlockPersonHimselfException("Попытка разблокировать самого себя");
        if (dst.getIsBlocked() == 2) throw new PersonNotFoundException("Попытка работы с удаленным пользователем");
        if (utilsService.isBlockedBy(src.getId(), dst.getId(), optionalFriendship))
            throw new NonBlockedFriendshipException("Пользователь не может разблокировать не заблокированного пользователя");
        Friendship friendship = optionalFriendship.orElseThrow(FriendshipNotFoundException::new);
        if (friendship.getFriendshipStatus().getFriendshipStatusType().equals(BLOCKED)) {
            friendshipStatusRepository.delete(utilsService.getFriendshipStatus(src.getId(), dst.getId()));
            friendshipStatusRepository.delete(utilsService.getFriendshipStatus(dst.getId(), src.getId()));
        } else if (friendship.getFriendshipStatus().getFriendshipStatusType().equals(INTERLOCKED)) {
            utilsService.createFriendship(src, dst, INTERLOCKED);
        }
        return utilsService.getMessageOkResponse();
    }

    @Override
    public ListDataResponse<Content> searchByPerson(String firstName, String lastName, Integer ageFrom, Integer ageTo, String country, String city, Integer offset, Integer limit, Principal principal) {
        Person currentUser = utilsService.findPersonByEmail(principal.getName());
        Pageable nextPage = PageRequest.of(offset, limit);
        Page<Person> personPage = personRepository.findAllByNameAndAgeAndLocation(currentUser.getId(), firstName, lastName, ageFrom, ageTo, country, city, nextPage);
        List<Content> data = personPage.getContent().stream()
                .map(p -> utilsService.getAuthData(p, null))
                .collect(Collectors.toList());
        return utilsService.getListDataResponse((int) personPage.getTotalElements(), offset, limit, data);
    }

    private DataResponse<AuthData> getPersonDataResponse(Person person) {
        return new DataResponse<AuthData>()
                .setTimestamp(utilsService.getTimestamp())
                .setError("string")
                .setData(utilsService.getAuthData(person, null));
    }

    private Person savePersonByRequest(Person person, EditProfileRequest editProfileRequest) {
        Person personById = personRepository.findPersonById(person.getId()).orElseThrow();
        String[] split = null;
        if (editProfileRequest.getBirthDate() != null) {
            split = editProfileRequest.getBirthDate().split("\\+");
        }
        personById
                .setFirstName(editProfileRequest.getFirstName() != null
                        ? editProfileRequest.getFirstName() : person.getFirstName())
                .setLastName(editProfileRequest.getLastName() != null
                        ? editProfileRequest.getLastName() : person.getLastName())
                .setPhone(editProfileRequest.getPhone() != null
                        ? editProfileRequest.getPhone() : person.getPhone())
                .setPhoto(editProfileRequest.getPhoto() != null
                        ? editProfileRequest.getPhoto() : person.getPhoto())
                .setAbout(editProfileRequest.getAbout() != null
                        ? editProfileRequest.getAbout() : person.getAbout())
                .setTown(editProfileRequest.getCity() != null
                        ? editProfileRequest.getCity() : person.getTown())
                .setCountry(editProfileRequest.getCountry() != null
                        ? editProfileRequest.getCountry() : person.getCountry())
                .setBirthDate(editProfileRequest.getBirthDate() != null
                        ? LocalDateTime.from(LocalDateTime.parse(Arrays.asList(split).get(0)).atZone(ZoneOffset.UTC)) : person.getBirthDate());
        personRepository.save(personById);
        return personById;
    }
}
