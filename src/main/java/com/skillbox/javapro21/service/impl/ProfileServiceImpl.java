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
import com.skillbox.javapro21.exception.PersonNotFoundException;
import com.skillbox.javapro21.repository.FriendshipRepository;
import com.skillbox.javapro21.repository.PersonRepository;
import com.skillbox.javapro21.repository.PostRepository;
import com.skillbox.javapro21.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.nio.file.ProviderNotFoundException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class ProfileServiceImpl implements ProfileService {

    private final UtilsService utilsService;
    private final PersonRepository personRepository;
    private final PostRepository postRepository;
    private final PostServiceImpl postService;
    private final FriendshipRepository friendshipRepository;

    @Autowired
    protected ProfileServiceImpl(UtilsService utilsService, PersonRepository personRepository, PostRepository postRepository, PostServiceImpl postService, FriendshipRepository friendshipRepository) {
        this.utilsService = utilsService;
        this.personRepository = personRepository;
        this.postRepository = postRepository;
        this.postService = postService;
        this.friendshipRepository = friendshipRepository;
    }

    public DataResponse<AuthData> getPerson(Principal principal) {
        Person person = utilsService.findPersonByEmail(principal.getName());
        return getDataResponse(person);
    }

    public DataResponse<AuthData> editPerson(Principal principal, EditProfileRequest editProfileRequest) {
        Person person = utilsService.findPersonByEmail(principal.getName());
        savePersonByRequest(person, editProfileRequest);
        return getDataResponse(person);
    }

    public DataResponse<AuthData> getPersonById(Long id) throws PersonNotFoundException {
        Person person = personRepository.findPersonById(id).orElseThrow(() -> new PersonNotFoundException("Пользователя с данным айди не существует"));
        return getDataResponse(person);
    }

    public ListDataResponse<PostData> getPersonWallById(Long id, int offset, int itemPerPage) {
        Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
        Page<Post> posts = postRepository.findPostsByPersonId(id, pageable);
        return postService.getPostsResponse(offset, itemPerPage, posts);
    }

    //:Todo добавить проверку на заблокированных?
    public DataResponse<PostData> postPostOnPersonWallById(Long id, Long publishDate, PostRequest postRequest, Principal principal) {
        Person person = utilsService.findPersonByEmail(principal.getName());
        Person personById = personRepository.findPersonById(id).orElseThrow(() -> new ProviderNotFoundException("Пользователя с данным id не существует"));
        Post post;
        if (person.getId().equals(personById.getId())) {
            post = new Post()
                    .setTitle(postRequest.getTitle())
                    .setPostText(postRequest.getPostText())
                    .setTime(utilsService.getLocalDateTime(publishDate))
                    .setIsBlocked(0)
                    .setAuthor(personById);
        } else {
            post = new Post()
                    .setTitle(postRequest.getTitle())
                    .setPostText(postRequest.getPostText())
                    .setTime(LocalDateTime.now())
                    .setIsBlocked(0)
                    .setAuthor(personById);
        }
        postRepository.save(post);
        return postService.getDataResponse(postService.getPostData(post));
    }

    public DataResponse<MessageOkContent> blockPersonById(Long id, Principal principal) throws BlockPersonHimselfException {
        Person person = utilsService.findPersonByEmail(principal.getName());
        Person personByBlock = personRepository.findPersonById(id).orElseThrow(() -> new ProviderNotFoundException("Пользователя с данным id не существует"));
        if (person.getId().equals(personByBlock.getId())) throw new BlockPersonHimselfException("Пользователь пытается заблокировать сам себя");
        if (personByBlock.getIsBlocked() == 0) {
            Optional<Friendship> optionalFriendship = friendshipRepository.findFriendshipBySrcPersonAndDstPerson(id, person.getId());
        }
        return null;
    }

    private boolean isBlockedBy(int blocker, int blocked, Optional<Friendship> optional) {
        return optional.filter(friendship -> (blocker == friendship.getSrcPerson().getId() && friendship.getFriendshipStatus().getFriendshipStatusType().equals(FriendshipStatusType.BLOCKED))
                || (blocked == friendship.getSrcPerson().getId() && friendship.getFriendshipStatus().getFriendshipStatusType().equals(FriendshipStatusType.BLOCKED))
                || friendship.getFriendshipStatus().getFriendshipStatusType().equals(FriendshipStatusType.BLOCKED)).isPresent();
    }

    private DataResponse<AuthData> getDataResponse(Person person) {
        return new DataResponse<AuthData>()
                .setTimestamp(LocalDateTime.now())
                .setError("string")
                .setData(utilsService.getAuthData(person, null));
    }

    private void savePersonByRequest(Person person, EditProfileRequest editProfileRequest) {
        person
                .setFirstName(editProfileRequest.getFirstName())
                .setLastName(editProfileRequest.getLastName())
                .setRegDate(editProfileRequest.getRegDate())
                .setBirthDate(editProfileRequest.getBirthDate())
                .setEmail(editProfileRequest.getEmail())
                .setPhone(editProfileRequest.getPhone())
                .setPhoto(editProfileRequest.getPhoto())
                .setAbout(editProfileRequest.getAbout())
                .setTown(editProfileRequest.getTown())
                .setCountry(editProfileRequest.getCountry());
        personRepository.save(person);
    }

    public DataResponse<MessageOkContent> deletePerson(Principal principal) {
        Person person = utilsService.findPersonByEmail(principal.getName())
                .setIsBlocked(2);
        personRepository.save(person);
        SecurityContextHolder.clearContext();
        return utilsService.getMessageOkResponse();
    }

}
