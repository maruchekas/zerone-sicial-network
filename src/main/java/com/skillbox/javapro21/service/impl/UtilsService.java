package com.skillbox.javapro21.service.impl;

import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.MessageOkContent;
import com.skillbox.javapro21.api.response.account.AuthData;
import com.skillbox.javapro21.domain.Friendship;
import com.skillbox.javapro21.domain.FriendshipStatus;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.domain.enumeration.FriendshipStatusType;
import com.skillbox.javapro21.exception.InterlockedFriendshipStatusException;
import com.skillbox.javapro21.repository.FriendshipRepository;
import com.skillbox.javapro21.repository.FriendshipStatusRepository;
import com.skillbox.javapro21.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static com.skillbox.javapro21.domain.enumeration.FriendshipStatusType.*;
import static com.skillbox.javapro21.domain.enumeration.FriendshipStatusType.SUBSCRIBED;

@Component
public class UtilsService {
    private final PersonRepository personRepository;
    private final FriendshipRepository friendshipRepository;
    private final FriendshipStatusRepository friendshipStatusRepository;

    @Autowired
    protected UtilsService(PersonRepository personRepository, FriendshipRepository friendshipRepository, FriendshipStatusRepository friendshipStatusRepository) {
        this.personRepository = personRepository;
        this.friendshipRepository = friendshipRepository;
        this.friendshipStatusRepository = friendshipStatusRepository;
    }

    /**
     * поиск пользователя по почте, если не найден выбрасывает ошибку
     */
    public Person findPersonByEmail(String email) {
        return personRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
    }

    /**
     * используется для ответа 200 "message: ok"
     */
    public DataResponse<MessageOkContent> getMessageOkResponse() {
        DataResponse<MessageOkContent> dataResponse = new DataResponse<>();
        dataResponse.setTimestamp(LocalDateTime.now());
        MessageOkContent accountData = new MessageOkContent();
        accountData.setMessage("ok");
        dataResponse.setData(accountData);
        return dataResponse;
    }

    /**
     * создание рандомного токена
     */
    public String getToken() {
        return new Random().ints(10, 33, 122)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }


    /**
     * заблокирован пользователь или нет ?
     */
    public String isBlockedPerson(Person person) {
        return person.getIsBlocked() == 0 ? "false" : "true";
    }

    /**
     * заполнение данных о пользователе
     */
    public AuthData getAuthData(Person person, String token) {
        AuthData authData = new AuthData()
                .setId(person.getId())
                .setFirstName(person.getFirstName())
                .setLastName(person.getLastName())
                .setRegDate(Timestamp.valueOf(person.getRegDate()))
                .setEmail(person.getEmail())
                .setMessagePermission(person.getMessagesPermission())
                .setLastOnlineTime(Timestamp.valueOf(person.getLastOnlineTime()))
                .setIsBlocked(isBlockedPerson(person))
                .setToken(token);
        if (person.getPhone() != null) authData.setPhone(person.getPhone());
        if (person.getPhoto() != null) authData.setPhoto(person.getPhoto());
        if (person.getAbout() != null) authData.setAbout(person.getAbout());
        if (person.getCountry() != null) {
            authData.setCity(Map.of("id", person.getId().toString(), "City", person.getTown()));
            authData.setCountry(Map.of("id", person.getId().toString(), "Country", person.getCountry()));
        }
        if (person.getBirthDate() != null) authData.setBirthDate(Timestamp.valueOf(person.getBirthDate()));

        return authData;
    }

    /**
     * получение LocalDateTime из TimestampAccessor, который отдает фронт
     */
    public LocalDateTime getLocalDateTime(long dateWithTimestampAccessor) {
        return LocalDateTime.parse(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        .format(new Date(dateWithTimestampAccessor)),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * проверка пользователей на статусы блокировок
     */
    public boolean isBlockedBy(Long blocker, Long blocked, Optional<Friendship> optional) {
        return optional.filter(friendship ->
                (blocker == friendship.getSrcPerson().getId() && friendship.getFriendshipStatus().getFriendshipStatusType().equals(FriendshipStatusType.BLOCKED))
                        || (blocked == friendship.getSrcPerson().getId() && friendship.getFriendshipStatus().getFriendshipStatusType().equals(WASBLOCKED))
                        || friendship.getFriendshipStatus().getFriendshipStatusType().equals(INTERLOCKED)).isPresent();
    }

    /**
     * создание отношений между пользователями
     */
    public void createFriendship(Person src, Person dst, FriendshipStatusType friendshipStatusType) throws InterlockedFriendshipStatusException {
        switch (friendshipStatusType) {
            case BLOCKED -> setFriendshipStatusBlocked(src, dst);
            case INTERLOCKED -> setFriendshipStatusBlocked(dst, src);
            case FRIEND, WASBLOCKED -> setOneFriendshipStatusTypeForSrcAndDst(src, dst, friendshipStatusType);
            case DECLINED, SUBSCRIBED, REQUEST -> setFriendshipStatusTypeForSrc(src, dst, friendshipStatusType);
        }
    }

    private void setFriendshipStatusBlocked(Person src, Person dst) {
        FriendshipStatus friendshipStatusSrc = getFriendshipStatusById(src.getId());
        friendshipStatusSrc.setTime(LocalDateTime.now());
        friendshipStatusSrc.setFriendshipStatusType(FriendshipStatusType.BLOCKED);
        friendshipStatusRepository.save(friendshipStatusSrc);

        FriendshipStatus friendshipStatusDst = getFriendshipStatusById(dst.getId());
        friendshipStatusDst.setTime(LocalDateTime.now());
        friendshipStatusDst.setFriendshipStatusType(WASBLOCKED);
        friendshipStatusRepository.save(friendshipStatusDst);

        Friendship friendshipSrc = friendshipRepository.findByPersonId(src.getId());
        friendshipSrc.setSrcPerson(src);
        friendshipSrc.setDstPerson(dst);
        friendshipSrc.setFriendshipStatus(friendshipStatusSrc);
        friendshipRepository.save(friendshipSrc);

        Friendship friendshipDst = friendshipRepository.findByPersonId(dst.getId());
        friendshipSrc.setSrcPerson(dst);
        friendshipSrc.setDstPerson(src);
        friendshipSrc.setFriendshipStatus(friendshipStatusDst);
        friendshipRepository.save(friendshipDst);
    }

    private void setFriendshipStatusTypeForSrc(Person src, Person dst, FriendshipStatusType friendshipStatusType) {
        FriendshipStatusType fst = null;
        if (friendshipStatusType.equals(FriendshipStatusType.DECLINED)) {
            fst = DECLINED;
        } else if (friendshipStatusType.equals(SUBSCRIBED)) {
            fst = SUBSCRIBED;
        } else if (friendshipStatusType.equals(REQUEST)) {
            fst = REQUEST;
        }
        FriendshipStatus friendshipStatusSrcDst = getFriendshipStatusById(src.getId());
        friendshipStatusSrcDst.setTime(LocalDateTime.now());
        friendshipStatusSrcDst.setFriendshipStatusType(fst);
        friendshipStatusRepository.save(friendshipStatusSrcDst);

        Friendship friendship = friendshipRepository.findByPersonId(src.getId());
        friendship.setSrcPerson(src);
        friendship.setDstPerson(dst);
        friendship.setFriendshipStatus(friendshipStatusSrcDst);
        friendshipRepository.save(friendship);
    }

    private void setOneFriendshipStatusTypeForSrcAndDst(Person src, Person dst, FriendshipStatusType friendshipStatusType) {
        FriendshipStatusType fst = null;
        if (friendshipStatusType.equals(FriendshipStatusType.WASBLOCKED)) {
            fst = INTERLOCKED;
        } else if (friendshipStatusType.equals(FRIEND)) {
            fst = FRIEND;
        }
        FriendshipStatus friendshipStatusSrcDst = getFriendshipStatusById(src.getId());
        friendshipStatusSrcDst.setTime(LocalDateTime.now());
        friendshipStatusSrcDst.setFriendshipStatusType(fst);
        friendshipStatusRepository.save(friendshipStatusSrcDst);

        Friendship friendshipSrc = friendshipRepository.findByPersonId(src.getId());
        friendshipSrc.setSrcPerson(src);
        friendshipSrc.setDstPerson(dst);
        friendshipSrc.setFriendshipStatus(friendshipStatusSrcDst);
        friendshipRepository.save(friendshipSrc);

        Friendship friendshipDst = friendshipRepository.findByPersonId(dst.getId());
        friendshipDst.setSrcPerson(dst);
        friendshipDst.setDstPerson(src);
        friendshipDst.setFriendshipStatus(friendshipStatusSrcDst);
        friendshipRepository.save(friendshipDst);
    }

    private FriendshipStatus getFriendshipStatusById(Long id) {
        return friendshipStatusRepository.findFriendshipStatusByPersonId(id);
    }
}
