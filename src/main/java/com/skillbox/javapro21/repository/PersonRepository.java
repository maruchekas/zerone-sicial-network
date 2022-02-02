package com.skillbox.javapro21.repository;

import com.skillbox.javapro21.domain.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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

    @Query("select p from Person p " +
            "left join PersonToDialog p2d on p2d.personId = p.id " +
            "left join Dialog d on p2d.dialogId = d.id " +
            "where p2d.dialogId = :id and p.isBlocked = 0 and d.isBlocked = 0 " +
            "group by p.id ")
    List<Person> findAllByDialogId(int id);

    @Query(value = "SELECT * FROM persons " +
                    "WHERE id != :currUserId " +
                    "AND id NOT IN (" +
                    "SELECT dst_person_id FROM friendship f " +
                    "JOIN friendship_statuses fs ON f.status_id = fs.id " +
                    "WHERE f.src_person_id = :currUserId " +
                    "AND fs.name IN ('BLOCKED', 'WASBLOCKED', 'INTERLOCKED')" +
                    ")" +
                    "AND first_name ILIKE CONCAT('%', :firstName, '%') " +
                    "AND last_name ILIKE CONCAT('%', :lastName, '%') " +
                    "AND DATE_PART('year', AGE(birth_date)) BETWEEN :ageFrom AND :ageTo " +
                    "AND country ILIKE CONCAT('%', :country, '%') " +
                    "AND town ILIKE CONCAT('%', :city, '%')" +
                    "AND is_blocked = 0", nativeQuery = true)
    Page<Person> findAllByNameAndAgeAndLocation(Long currUserId, String firstName, String lastName, Integer ageFrom, Integer ageTo, String country, String city, Pageable page);


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
    Page<Person> findAllRequest(Long id, Pageable pageable);

    @Query("select p from Person p " +
            "join Friendship f on f.dstPerson.id = p.id " +
            "join FriendshipStatus fs on fs.id = f.friendshipStatus.id " +
            "where f.srcPerson.id = :id " +
            "and p.firstName = :name " +
            "and fs.friendshipStatusType = 'REQUEST' " +
            "and p.isBlocked = 0 " +
            "order by p.firstName asc")
    Page<Person> findAllRequestByName(Long id, String name, Pageable pageable);

    @Query("select p from Person p " +
            "join Friendship f on f.dstPerson.id = p.id " +
            "join FriendshipStatus fs on fs.id = f.friendshipStatus.id " +
            "where f.srcPerson.id in (:idsFriends) " +
            "and p.id not in (:id) " +
            "and fs.friendshipStatusType = 'FRIEND' " +
            "and p.isBlocked = 0 " +
            "order by p.firstName asc")
    Page<Person> findRecommendedFriendsByPerson(Long id, List<Long> idsFriends, Pageable pageable);
}
