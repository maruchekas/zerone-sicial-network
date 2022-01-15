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
            "left join Friendship f on f.srcPerson.id = p.id " +
            "left join FriendshipStatus fs on fs.id = f.friendshipStatus.id " +
            "where f.srcPerson.id = :id " +
            "and ( " +
            "fs.friendshipStatusType = 'FRIEND' " +
            "or fs.friendshipStatusType = 'SUBSCRIBED' " +
            ") group by p.id" )
    List<Long> findAllFriendsAndSubscribersByPersonId(Long id);

    @Query(value =
            "SELECT * FROM persons " +
            "WHERE id != :currUserId " +
                    "AND id NOT IN (" +
                        "SELECT dst_person_id FROM friendship f " +
                        "JOIN friendship_statuses fs ON f.status_id = fs.id " +
                        "WHERE f.src_person_id = :currUserId " +
                            "AND fs.name IN ('BLOCKED', 'WASBLOCKED', 'INTERLOCKED')" +
                    ")" +
                    "AND first_name LIKE CONCAT('%', :firstName, '%') " +
                    "AND last_name LIKE CONCAT('%', :lastName, '%') " +
                    "AND DATE_PART('year', AGE(birth_date)) BETWEEN :ageFrom AND :ageTo " +
                    "AND country LIKE CONCAT('%', :country, '%') " +
                    "AND town LIKE CONCAT('%', :city, '%')" +
                    "AND is_blocked = 0",
            nativeQuery = true)
    Page<Person> findAllByNameAndAgeAndLocation(Long currUserId, String firstName, String lastName, Integer ageFrom, Integer ageTo, String country, String city, Pageable page);
}
