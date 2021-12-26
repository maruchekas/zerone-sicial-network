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
            "WHERE p.email = ?1")
    Optional<Person> findByEmail(String email);

    @Query("select p.id from Person p " +
            "left join Friendship f on f.dstPerson.id = p.id or f.srcPerson.id = p.id " +
            "left join FriendshipStatus fs on fs.id = f.friendshipStatus.id " +
            "where (" +
            "(f.srcPerson.id = :id and fs.friendshipStatusType = ('BLOCKED') ) " +
            "or " +
            "(f.dstPerson.id = :id and fs.friendshipStatusType = ('BLOCKED') ) " +
            ") " +
            "and p.isBlocked = 0 " +
            "and p.id = :id " +
            "order by p.id asc")
    List<Integer> findBlockersId(Long id);
}
