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
import com.skillbox.javapro21.domain.Tag;
import com.skillbox.javapro21.domain.enumeration.FriendshipStatusType;
import com.skillbox.javapro21.exception.*;
import com.skillbox.javapro21.repository.FriendshipRepository;
import com.skillbox.javapro21.repository.FriendshipStatusRepository;
import com.skillbox.javapro21.repository.PersonRepository;
import com.skillbox.javapro21.repository.PostRepository;
import com.skillbox.javapro21.service.ProfileService;
import com.skillbox.javapro21.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.sql.ResultSet;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.skillbox.javapro21.config.Constants.USER_NOT_FOUND_ERR;
import static com.skillbox.javapro21.domain.enumeration.FriendshipStatusType.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final UtilsService utilsService;
    private final PostServiceImpl postService;
    private final TagService tagService;
    private final PersonRepository personRepository;
    private final PostRepository postRepository;
    private final JdbcTemplate jdbcTemplate;
    private final FriendshipRepository friendshipRepository;
    private final FriendshipStatusRepository friendshipStatusRepository;

    @Override
    @Cacheable(value = "person", key = "#principal.name")
    public DataResponse<AuthData> getPerson(Principal principal) {
        log.info("caching person " + principal.getName());
        Person person = utilsService.findPersonByEmail(principal.getName());
        return getPersonDataResponse(person);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "person", key = "#principal.name", allEntries = true),
            @CacheEvict(value = "persons", allEntries = true)
    })
    public DataResponse<AuthData> editPerson(Principal principal, EditProfileRequest editProfileRequest) {
        Person person = utilsService.findPersonByEmail(principal.getName());
        Person sPerson = savePersonByRequest(person, editProfileRequest);
        return getPersonDataResponse(sPerson);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "person", key = "#principal.name", allEntries = true),
            @CacheEvict(value = "persons", allEntries = true)
    })
    public DataResponse<MessageOkContent> deletePerson(Principal principal) {
        Person person = utilsService.findPersonByEmail(principal.getName())
                .setIsBlocked(2);
        personRepository.save(person);
        SecurityContextHolder.clearContext();
        return utilsService.getMessageOkResponse();
    }

    @Override
    public DataResponse<AuthData> getPersonById(Long id, Principal principal)
            throws PersonNotFoundException, InterlockedFriendshipStatusException {
        Person src = utilsService.findPersonByEmail(principal.getName());
        Person dst = personRepository.findPersonById(id)
                .orElseThrow(() -> new PersonNotFoundException(USER_NOT_FOUND_ERR));
        Optional<Friendship> directRelation = friendshipRepository.findFriendshipBySrcPersonAndDstPerson(src.getId()
                , id);

        if (isBlockedFor(dst.getId(), src.getId(), directRelation)) {
            throw new InterlockedFriendshipStatusException();
        }

        Person person = personRepository.findPersonById(id)
                .orElseThrow(() -> new PersonNotFoundException(USER_NOT_FOUND_ERR));

        return getPersonDataResponse(person);
    }

    @Override
    public ListDataResponse<PostData> getPersonWallById(Long id, int offset, int itemPerPage, Principal principal)
            throws PersonNotFoundException, InterlockedFriendshipStatusException {

        Person src = utilsService.findPersonByEmail(principal.getName());
        Person dst = personRepository.findPersonById(id)
                .orElseThrow(() -> new PersonNotFoundException(USER_NOT_FOUND_ERR));
        Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
        Optional<Friendship> optionalFriendship = friendshipRepository.findFriendshipBySrcPersonAndDstPerson(src.getId(), id);
        Page<Post> posts = postRepository.findPostsByAuthorId(id, pageable);

        if (isBlockedFor(dst.getId(), src.getId(), optionalFriendship)) {
            throw new InterlockedFriendshipStatusException();
        }
        if (src.getId().equals(id)) {
            return postService.getPostsResponse(offset, itemPerPage, posts, src);
        }
        posts = postRepository.findPostsByPersonId(id, pageable);
        return postService.getPostsResponse(offset, itemPerPage, posts, src);

    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "person", key = "#principal.name", allEntries = true),
            @CacheEvict(value = "persons", allEntries = true)
    })
    public DataResponse<PostData> postPostOnPersonWallById(Long id, Long publishDate, PostRequest postRequest,
                                                           Principal principal)
            throws InterlockedFriendshipStatusException, PersonNotFoundException, PostNotFoundException {
        Person src = utilsService.findPersonByEmail(principal.getName());
        Person dst = personRepository.findPersonById(id)
                .orElseThrow(() -> new PersonNotFoundException(USER_NOT_FOUND_ERR));
        Post post;
        Set<Tag> tags = tagService.addTagsToPost(postRequest.getTags());
        if (src.getId().equals(id)) {
            post = new Post()
                    .setTitle(postRequest.getTitle())
                    .setPostText(postRequest.getPostText())
                    .setIsBlocked(0)
                    .setTags(tags)
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
                        .setTags(tags)
                        .setAuthor(dst);
            } else throw new InterlockedFriendshipStatusException();
        }
        postRepository.save(post);
        return postService.getDataResponse(postService.getPostData(post, src));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "person", key = "#principal.name", allEntries = true),
            @CacheEvict(value = "persons", allEntries = true)
    })
    public DataResponse<MessageOkContent> blockPersonById(Long id, Principal principal)
            throws InterlockedFriendshipStatusException, PersonNotFoundException, FriendshipNotFoundException {
        Person src = utilsService.findPersonByEmail(principal.getName());
        Person dst = personRepository.findPersonById(id)
                .orElseThrow(() -> new PersonNotFoundException(USER_NOT_FOUND_ERR));
        if (!src.getId().equals(dst.getId())) {
            Optional<Friendship> optionalFriendship = friendshipRepository.findFriendshipBySrcPersonAndDstPerson(src.getId(), id);
            if (optionalFriendship.isEmpty() || !optionalFriendship.get().getFriendshipStatus().getFriendshipStatusType().equals(BLOCKED)) {
                utilsService.createFriendship(src, dst, BLOCKED);
            } else {
                if (optionalFriendship.get().getFriendshipStatus().getFriendshipStatusType().equals(BLOCKED)) {
                    friendshipStatusRepository.delete(utilsService.getFriendshipStatus(src.getId(), dst.getId()));
                    friendshipStatusRepository.delete(utilsService.getFriendshipStatus(dst.getId(), src.getId()));
                } else throw new InterlockedFriendshipStatusException();
            }
        }
        return utilsService.getMessageOkResponse();
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "person", key = "#principal.name", allEntries = true),
            @CacheEvict(value = "persons", allEntries = true)
    })
    public DataResponse<MessageOkContent> unblockPersonById(Long id, Principal principal)
            throws PersonNotFoundException, BlockPersonHimselfException, NonBlockedFriendshipException, FriendshipNotFoundException {
        Person src = utilsService.findPersonByEmail(principal.getName());
        Person dst = personRepository.findPersonById(id)
                .orElseThrow(() -> new PersonNotFoundException(USER_NOT_FOUND_ERR));
        Optional<Friendship> optionalFriendship = friendshipRepository.findFriendshipBySrcPersonAndDstPerson(src.getId(), id);
        if (src.getId().equals(id)) throw new BlockPersonHimselfException("Попытка разблокировать самого себя");
        if (dst.getIsBlocked() == 2) throw new PersonNotFoundException("Попытка работы с удаленным пользователем");
        if (utilsService.isBlockedBy(src.getId(), dst.getId(), optionalFriendship))
            throw new NonBlockedFriendshipException("Пользователь не может разблокировать не заблокированного пользователя");
        Friendship friendship = optionalFriendship
                .orElseThrow(() -> new FriendshipNotFoundException("Отношений с данным id не существует"));
        if (friendship.getFriendshipStatus().getFriendshipStatusType().equals(BLOCKED)) {
            friendshipStatusRepository.delete(utilsService.getFriendshipStatus(src.getId(), dst.getId()));
            friendshipStatusRepository.delete(utilsService.getFriendshipStatus(dst.getId(), src.getId()));
        } else if (friendship.getFriendshipStatus().getFriendshipStatusType().equals(INTERLOCKED)) {
            utilsService.createFriendship(src, dst, BLOCKED);
            utilsService.createFriendship(dst, src, WASBLOCKED);
        }
        return utilsService.getMessageOkResponse();
    }

    @Override
    @Cacheable(value = "persons", key = "#firstName + #lastName + #ageFrom + #ageTo + #country + #city + #offset + #limit + #principal.name")
    public ListDataResponse<Content> searchByPerson(String firstName, String lastName, Integer ageFrom, Integer ageTo,
                                                    String country, String city,
                                                    Integer offset, Integer limit, Principal principal) {

        log.info("caching persons search for " + principal.getName());
        Person currentUser = utilsService.findPersonByEmail(principal.getName());
        Pageable nextPage = PageRequest.of(offset, limit);
        String selectAge = (ageFrom == 0 || ageTo == 150)
                ? "AND (DATE_PART('year', AGE(birth_date)) BETWEEN (?) AND (?) OR birth_date IS NULL) "
                : "AND DATE_PART('year', AGE(birth_date)) BETWEEN (?) AND (?) ";
        String selectCountry = country.isBlank()
                ? "AND (country ILIKE CONCAT('%', (?), '%') OR country IS NULL) "
                : "AND country ILIKE CONCAT('%', (?), '%') ";
        String selectCity = city.isBlank()
                ? "AND (town ILIKE CONCAT('%', (?), '%') OR town IS NULL) "
                : "AND town ILIKE CONCAT('%', (?), '%') ";
        String query = "(" +
                "SELECT id FROM persons " +
                "WHERE id != (?) " +
                "AND id NOT IN (" +
                "SELECT dst_person_id FROM friendship f " +
                "JOIN friendship_statuses fs ON f.status_id = fs.id " +
                "WHERE f.src_person_id = (?) " +
                "AND fs.name IN ('BLOCKED', 'WASBLOCKED', 'INTERLOCKED')" +
                ")" +
                "AND first_name ILIKE CONCAT('%', (?), '%') " +
                "AND last_name ILIKE CONCAT('%', (?), '%') " +
                selectAge +
                selectCountry +
                selectCity +
                "AND is_blocked = 0" +
                ")";
        List<Long> ids = jdbcTemplate.query(query,
                (ResultSet rs, int rowNum) -> rs.getLong("id"),
                currentUser.getId(), currentUser.getId(), firstName, lastName, ageFrom, ageTo, country, city);
        if (ids.isEmpty()) {
            firstName = utilsService.convertKbLayer(firstName);
            lastName = utilsService.convertKbLayer(lastName);
            ids = jdbcTemplate.query(query,
                    (ResultSet rs, int rowNum) -> rs.getLong("id"),
                    currentUser.getId(), currentUser.getId(), firstName, lastName, ageFrom, ageTo, country, city);
        }
        Page<Person> personPage = personRepository.findAllValidById(ids, nextPage);
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
                .setBirthDate(editProfileRequest.getBirthDate() != 0
                        ? LocalDateTime.ofInstant(Instant.ofEpochMilli(editProfileRequest.getBirthDate()), ZoneOffset.UTC) : person.getBirthDate());
        personRepository.save(personById);
        return personById;
    }

    public boolean isBlockedFor(Long blocker, Long blocked, Optional<Friendship> optional) {
        return optional.filter(friendship ->
                (Objects.equals(blocker, friendship.getDstPerson().getId())
                        && Objects.equals(blocked, friendship.getSrcPerson().getId())
                        && (friendship.getFriendshipStatus().getFriendshipStatusType().equals(FriendshipStatusType.WASBLOCKED)
                        || friendship.getFriendshipStatus().getFriendshipStatusType().equals(INTERLOCKED)))).isPresent();
    }

}
