package com.skillbox.javapro21.repository;

import com.skillbox.javapro21.domain.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {
    @Query("SELECT p FROM Person p " +
            "WHERE p.email = ?1")
    Optional<Person> findByEmail(String email);

    @Query("SELECT p from Person p where p.id = :id and p.isBlocked = 0")
    Optional<Person> findPersonById(Long id);
}
