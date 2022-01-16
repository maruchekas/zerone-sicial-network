package com.skillbox.javapro21.repository;

import com.skillbox.javapro21.domain.Person;
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
            "left join Friendship f on f.srcPerson.id = p.id " +
            "left join FriendshipStatus fs on fs.id = f.friendshipStatus.id " +
            "where f.srcPerson.id = :id " +
            "and ( " +
            "fs.friendshipStatusType = 'FRIEND' " +
            "or fs.friendshipStatusType = 'SUBSCRIBED' " +
            ") group by p.id" )
    List<Long> findAllFriendsAndSubscribersByPersonId(Long id);

    @Query("select p from Person p where p.id in (:ids) and p.isBlocked = 0")
    List<Person> findAllById(List<Long> ids);

    @Query("select p from Person p " +
            "left join PersonToDialog p2d on p2d.personId = p.id " +
            "left join Dialog d on p2d.dialogId = d.id " +
            "where p2d.dialogId = :id and p.isBlocked = 0 and d.isBlocked = 0 " +
            "group by p.id ")
    List<Person> findAllByDialogId(int id);
}
