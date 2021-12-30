package com.skillbox.javapro21.service.impl;

import com.skillbox.javapro21.api.request.post.PostRequest;
import com.skillbox.javapro21.api.request.profile.EditProfileRequest;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.MessageOkContent;
import com.skillbox.javapro21.api.response.account.AuthData;
import com.skillbox.javapro21.api.response.post.PostData;
import com.skillbox.javapro21.domain.Friendship;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.domain.Post;
import com.skillbox.javapro21.domain.enumeration.FriendshipStatusType;
import com.skillbox.javapro21.exception.BlockPersonHimselfException;
import com.skillbox.javapro21.exception.InterlockedFriendshipStatusException;
import com.skillbox.javapro21.exception.NonBlockedFriendshipException;
import com.skillbox.javapro21.exception.PersonNotFoundException;
import com.skillbox.javapro21.repository.FriendshipRepository;
import com.skillbox.javapro21.repository.FriendshipStatusRepository;
import com.skillbox.javapro21.repository.PersonRepository;
import com.skillbox.javapro21.repository.PostRepository;
import com.skillbox.javapro21.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.skillbox.javapro21.domain.enumeration.FriendshipStatusType.*;

@Component
public class ProfileServiceImpl implements ProfileService {

    private final UtilsService utilsService;
    private final PersonRepository personRepository;
    private final PostRepository postRepository;
    private final PostServiceImpl postService;
    private final FriendshipRepository friendshipRepository;
    private final FriendshipStatusRepository friendshipStatusRepository;

    @Autowired
    protected ProfileServiceImpl(UtilsService utilsService, PersonRepository personRepository, PostRepository postRepository, PostServiceImpl postService, FriendshipRepository friendshipRepository, FriendshipStatusRepository friendshipStatusRepository) {
        this.utilsService = utilsService;
        this.personRepository = personRepository;
        this.postRepository = postRepository;
        this.postService = postService;
        this.friendshipRepository = friendshipRepository;
        this.friendshipStatusRepository = friendshipStatusRepository;
    }

    public DataResponse<AuthData> getPerson(Principal principal) {
        Person person = utilsService.findPersonByEmail(principal.getName());
        return getPersonDataResponse(person);
    }

    public DataResponse<AuthData> editPerson(Principal principal, EditProfileRequest editProfileRequest) {
        Person person = utilsService.findPersonByEmail(principal.getName());
        savePersonByRequest(person, editProfileRequest);
        return getPersonDataResponse(person);
    }

    public DataResponse<MessageOkContent> deletePerson(Principal principal) {
        Person person = utilsService.findPersonByEmail(principal.getName())
                .setIsBlocked(2);
        personRepository.save(person);
        SecurityContextHolder.clearContext();
        return utilsService.getMessageOkResponse();
    }

    public DataResponse<AuthData> getPersonById(Long id) throws PersonNotFoundException {
        Person person = personRepository.findPersonById(id).orElseThrow(() -> new PersonNotFoundException("Пользователя с данным айди не существует"));
        return getPersonDataResponse(person);
    }

    public ListDataResponse<PostData> getPersonWallById(Long id, int offset, int itemPerPage, Principal principal) throws PersonNotFoundException, InterlockedFriendshipStatusException {
        Person src = utilsService.findPersonByEmail(principal.getName());
        Person dst = personRepository.findPersonById(id).orElseThrow(() -> new PersonNotFoundException("Пользователя с данным id не существует"));
        Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
        Optional<Friendship> optionalFriendship = friendshipRepository.findFriendshipBySrcPersonAndDstPerson(src.getId(), id);
        if (!utilsService.isBlockedBy(src.getId(), dst.getId(), optionalFriendship)) {
            Page<Post> posts = postRepository.findPostsByPersonId(id, pageable);
            return postService.getPostsResponse(offset, itemPerPage, posts);
        }
        throw new InterlockedFriendshipStatusException("Полльзователь заблокирован и не может смотреть посты");
    }

    public DataResponse<PostData> postPostOnPersonWallById(Long id, Long publishDate, PostRequest postRequest, Principal principal) throws InterlockedFriendshipStatusException, PersonNotFoundException {
        Person src = utilsService.findPersonByEmail(principal.getName());
        Person dst = personRepository.findPersonById(id).orElseThrow(() -> new PersonNotFoundException("Пользователя с данным id не существует"));
        Post post;
        if (src.getId().equals(id)) {
            post = new Post()
                    .setTitle(postRequest.getTitle())
                    .setPostText(postRequest.getPostText())
                    .setTime(utilsService.getLocalDateTime(publishDate))
                    .setIsBlocked(0)
                    .setAuthor(dst);
        } else {
            Optional<Friendship> optionalFriendship = friendshipRepository.findFriendshipBySrcPersonAndDstPerson(src.getId(), id);
            if (!utilsService.isBlockedBy(src.getId(), dst.getId(), optionalFriendship)) {
                post = new Post()
                        .setTitle(postRequest.getTitle())
                        .setPostText(postRequest.getPostText())
                        .setTime(LocalDateTime.now())
                        .setIsBlocked(0)
                        .setAuthor(dst);
            } else throw new InterlockedFriendshipStatusException("Один из пользователей заблокирован для другого");
        }
        postRepository.save(post);
        return postService.getDataResponse(postService.getPostData(post));

    }

    public DataResponse<MessageOkContent> blockPersonById(Long id, Principal principal) throws BlockPersonHimselfException, InterlockedFriendshipStatusException, PersonNotFoundException {
        Person src = utilsService.findPersonByEmail(principal.getName());
        Person dst = personRepository.findPersonById(id).orElseThrow(() -> new PersonNotFoundException("Пользователя с данным id не существует"));
        if (src.getId().equals(dst.getId()))
            throw new BlockPersonHimselfException("Пользователь пытается заблокировать сам себя");
        Optional<Friendship> optionalFriendship = friendshipRepository.findFriendshipBySrcPersonAndDstPerson(src.getId(), id);
        Friendship friendship = optionalFriendship.orElseThrow(EntityNotFoundException::new);
        if (!utilsService.isBlockedBy(src.getId(), dst.getId(), optionalFriendship)) {
            if (!friendship.getFriendshipStatus().getFriendshipStatusType().equals(BLOCKED)) {
                utilsService.createFriendship(src, dst, FriendshipStatusType.BLOCKED);
            } else if (friendship.getFriendshipStatus().getFriendshipStatusType().equals(WASBLOCKED)) {
                utilsService.createFriendship(src, dst, FriendshipStatusType.WASBLOCKED);
            } else throw new InterlockedFriendshipStatusException("Уже взаимно заблокированы");
        }
        return utilsService.getMessageOkResponse();
    }

    public DataResponse<MessageOkContent> unblockPersonById(Long id, Principal principal) throws PersonNotFoundException, BlockPersonHimselfException, NonBlockedFriendshipException {
        Person src = utilsService.findPersonByEmail(principal.getName());
        Person dst = personRepository.findPersonById(id).orElseThrow(() -> new PersonNotFoundException("Пользователя с данным id не существует"));
        Optional<Friendship> optionalFriendship = friendshipRepository.findFriendshipBySrcPersonAndDstPerson(src.getId(), id);
        if (src.getId().equals(id)) throw new BlockPersonHimselfException("Попытка разблокировать самого себя");
        if (dst.getIsBlocked() == 2) throw new PersonNotFoundException("Попытка работы с удаленным пользователем");
        if (!utilsService.isBlockedBy(src.getId(), dst.getId(), optionalFriendship))
            throw new NonBlockedFriendshipException("Пользователь не может разблокировать не заблокированного пользователя");
        Friendship friendship = optionalFriendship.orElseThrow(EntityNotFoundException::new);
        if (friendship.getFriendshipStatus().equals(BLOCKED)) {
            friendshipRepository.delete(friendship);
        } else if (friendship.getFriendshipStatus().equals(INTERLOCKED)) {

        }
        return utilsService.getMessageOkResponse();
    }

    private DataResponse<AuthData> getPersonDataResponse(Person person) {
        return new DataResponse<AuthData>()
                .setTimestamp(LocalDateTime.now())
                .setError("string")
                .setData(utilsService.getAuthData(person, null));
    }

    private void savePersonByRequest(Person person, EditProfileRequest editProfileRequest) {
        person
                .setFirstName(editProfileRequest.getFirstName()!= null
                        ? editProfileRequest.getFirstName() : person.getFirstName())
                .setLastName(editProfileRequest.getLastName() != null
                        ? editProfileRequest.getLastName() : person.getLastName())
                .setBirthDate(editProfileRequest.getBirthDate() != null
                        ? editProfileRequest.getBirthDate() : person.getBirthDate())
                .setEmail(editProfileRequest.getEmail() != null
                ? editProfileRequest.getEmail() : person.getEmail())
                .setPhone(editProfileRequest.getPhone() != null
                ? editProfileRequest.getPhone() : person.getPhone())
                .setPhoto(editProfileRequest.getPhoto() != null
                ? editProfileRequest.getPhoto() : person.getPhoto())
                .setAbout(editProfileRequest.getAbout() != null
                ? editProfileRequest.getAbout() : person.getAbout())
                .setTown(editProfileRequest.getTown() != null
                ? editProfileRequest.getTown() : person.getTown())
                .setCountry(editProfileRequest.getCountry() != null
                ? editProfileRequest.getCountry() : person.getCountry());
        personRepository.save(person);
    }

}
