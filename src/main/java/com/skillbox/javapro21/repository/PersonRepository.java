package com.skillbox.javapro21.repository;

import com.skillbox.javapro21.domain.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {
    @Query("SELECT p FROM Person p " +
            "WHERE p.email = :email")
    Optional<Person> findByEmail(String email);

    @Query("SELECT p from Person p where p.id = :id and p.isBlocked = 0")
    Optional<Person> findPersonById(Long id);

    @Query("select p.id from Person p " +
            "join Friendship f on f.dstPerson.id = p.id " +
            "join FriendshipStatus fs on fs.id = f.friendshipStatus.id " +
            "where f.srcPerson.id = :id " +
            "and ( fs.friendshipStatusType = 'FRIEND' or fs.friendshipStatusType = 'SUBSCRIBED' ) " +
            "and p.isBlocked = 0" )
    List<Long> findAllFriendsAndSubscribersByPersonId(Long id);

    @Query("select p.id from Person p " +
            "join Friendship f on f.dstPerson.id = p.id " +
            "join FriendshipStatus fs on fs.id = f.friendshipStatus.id " +
            "where f.srcPerson.id = :id " +
            "and fs.friendshipStatusType = 'FRIEND' " +
            "and p.isBlocked = 0" )
    List<Long> findAllFriendsByPersonId(Long id);

    @Query("select p from Person p where p.id in (:ids) and p.isBlocked = 0")
    List<Person> findAllById(List<Long> ids);

    @Query("SELECT p FROM Person p WHERE p.id IN (:ids)")
    Page<Person> findAllValidById(List<Long> ids, Pageable pageable);

    @Query("select p from Person p " +
            "left join PersonToDialog p2d on p2d.personId = p.id " +
            "left join Dialog d on p2d.dialogId = d.id " +
            "where p2d.dialogId = :id and p.isBlocked = 0 and d.isBlocked = 0 " +
            "group by p.id ")
    List<Person> findAllByDialogId(int id);

    @Query("select p.id from Person p " +
            "join Friendship f on f.dstPerson.id = p.id " +
            "join FriendshipStatus fs on fs.id = f.friendshipStatus.id " +
            "where f.srcPerson.id = :id " +
            "and ( fs.friendshipStatusType = 'BLOCKED' or fs.friendshipStatusType = 'INTERLOCKED') " +
            "and ( p.isBlocked = 1 or p.isBlocked = 2 ) " )
    List<Long> findAllBlocksPersons(Long id);

    @Query("select p from Person p " +
            "join Friendship f on f.dstPerson.id = p.id " +
            "join FriendshipStatus fs on fs.id = f.friendshipStatus.id " +
            "where f.srcPerson.id = :id " +
            "and fs.friendshipStatusType = 'FRIEND' " +
            "and p.isBlocked = 0 " +
            "order by p.firstName asc")
    Page<Person> findAllPersonFriends(Long id, Pageable pageable);

    @Query("select p from Person p " +
            "join Friendship f on f.dstPerson.id = p.id " +
            "join FriendshipStatus fs on fs.id = f.friendshipStatus.id " +
            "where f.srcPerson.id = :id " +
            "and p.firstName = :name " +
            "and fs.friendshipStatusType = 'FRIEND' " +
            "and p.isBlocked = 0 " +
            "order by p.firstName asc")
    Page<Person> findAllPersonFriendsAndName(Long id, String name, Pageable pageable);

    @Query("select p from Person p " +
            "join Friendship f on f.dstPerson.id = p.id " +
            "join FriendshipStatus fs on fs.id = f.friendshipStatus.id " +
            "where f.srcPerson.id = :id " +
            "and fs.friendshipStatusType = 'REQUEST' " +
            "and p.isBlocked = 0 " +
            "order by p.firstName asc")
    Page<Person> findAllIncomingRequests(Long id, Pageable pageable);

    @Query("select p from Person p " +
            "join Friendship f on f.dstPerson.id = p.id " +
            "join FriendshipStatus fs on fs.id = f.friendshipStatus.id " +
            "where f.srcPerson.id = :id " +
            "and p.firstName = :name " +
            "and fs.friendshipStatusType = 'REQUEST' " +
            "and p.isBlocked = 0 " +
            "order by p.firstName asc")
    Page<Person> findAllIncomingRequestsByName(Long id, String name, Pageable pageable);

    @Query("select p from Person p " +
            "join Friendship f on f.srcPerson.id = p.id " +
            "join FriendshipStatus fs on fs.id = f.friendshipStatus.id " +
            "where f.dstPerson.id = :id " +
            "and fs.friendshipStatusType = 'REQUEST' " +
            "and p.isBlocked = 0 " +
            "order by p.firstName asc")
    Page<Person> findAllOutgoingRequests(Long id, Pageable pageable);

    @Query("select p from Person p " +
            "join Friendship f on f.srcPerson.id = p.id " +
            "join FriendshipStatus fs on fs.id = f.friendshipStatus.id " +
            "where f.dstPerson.id = :id " +
            "and p.firstName = :name " +
            "and fs.friendshipStatusType = 'REQUEST' " +
            "and p.isBlocked = 0 " +
            "order by p.firstName asc")
    Page<Person> findAllOutgoingRequestsByName(Long id, String name, Pageable pageable);

    @Query("select p from Person p " +
            "join Friendship f on f.dstPerson.id = p.id " +
            "join FriendshipStatus fs on fs.id = f.friendshipStatus.id " +
            "where f.srcPerson.id = :id " +
            "and (fs.friendshipStatusType = 'BLOCKED' or fs.friendshipStatusType = 'INTERLOCKED') " +
            "and p.isBlocked = 0 ")
    Page<Person> findAllBlockedPersons(Long id, Pageable pageable);

    @Query("select p from Person p " +
            "join Friendship f on f.dstPerson.id = p.id " +
            "join FriendshipStatus fs on fs.id = f.friendshipStatus.id " +
            "where f.srcPerson.id = :id " +
            "and p.firstName = :name " +
            "and (fs.friendshipStatusType = 'BLOCKED' or fs.friendshipStatusType = 'INTERLOCKED')" +
            "and p.isBlocked = 0 " +
            "order by p.firstName asc")
    Page<Person> findAllBlockedPersonsByName(Long id, String name, Pageable pageable);

    @Query("select p from Person p " +
            "join Friendship f on f.dstPerson.id = p.id " +
            "join FriendshipStatus fs on fs.id = f.friendshipStatus.id " +
            "where f.srcPerson.id = :id " +
            "and fs.friendshipStatusType = 'SUBSCRIBED' " +
            "and p.isBlocked = 0 ")
    Page<Person> findAllSubscribers(Long id, Pageable pageable);

    @Query("select p from Person p " +
            "join Friendship f on f.dstPerson.id = p.id " +
            "join FriendshipStatus fs on fs.id = f.friendshipStatus.id " +
            "where f.srcPerson.id = :id " +
            "and p.firstName = :name " +
            "and fs.friendshipStatusType = 'SUBSCRIBED' " +
            "and p.isBlocked = 0 " +
            "order by p.firstName asc")
    Page<Person> findAllSubscribersByName(Long id, String name, Pageable pageable);

    @Query("select p from Person p " +
            "join Friendship f on f.dstPerson.id = p.id " +
            "join FriendshipStatus fs on fs.id = f.friendshipStatus.id " +
            "where f.srcPerson.id = :id " +
            "and fs.friendshipStatusType = 'DECLINED' " +
            "and p.isBlocked = 0 ")
    Page<Person> findAllSubscriptions(Long id, Pageable pageable);

    @Query("select p from Person p " +
            "join Friendship f on f.dstPerson.id = p.id " +
            "join FriendshipStatus fs on fs.id = f.friendshipStatus.id " +
            "where f.srcPerson.id = :id " +
            "and p.firstName = :name " +
            "and fs.friendshipStatusType = 'DECLINED' " +
            "and p.isBlocked = 0 " +
            "order by p.firstName asc")
    Page<Person> findAllSubscriptionsByName(Long id, String name, Pageable pageable);

    @Query("select p from Person p " +
            "join Friendship f on f.dstPerson.id = p.id " +
            "join FriendshipStatus fs on fs.id = f.friendshipStatus.id " +
            "where f.srcPerson.id in (:idsFriends) " +
            "and p.id not in (:id) " +
            "and fs.friendshipStatusType = 'FRIEND' " +
            "and p.isBlocked = 0 " +
            "order by p.firstName asc")
    Page<Person> findRecommendedFriendsByPerson(Long id, List<Long> idsFriends, Pageable pageable);

    @Query("select count(p) from Person p " +
            "where p.isBlocked = 0")
    Long findCountPerson();

    @Query("select p from Person p " +
            "where p.isBlocked = 0")
    List<Person> findAllPersons();

    @Query("select p from Person p " +
            "where p.birthDate between :from and :before " +
            "and p.isBlocked = 0")
    List<Person> findAllPersonsByYearsOld(LocalDateTime from, LocalDateTime before);

    @Query("select p from Person p " +
            "where p.isBlocked = 0 " +
            "and p.birthDate is not null")
    List<Person> findAllPersonsWithBirthday();

    Optional<Person> findByConfirmationCode(String token);
}
