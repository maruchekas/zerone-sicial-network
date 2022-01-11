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
}
