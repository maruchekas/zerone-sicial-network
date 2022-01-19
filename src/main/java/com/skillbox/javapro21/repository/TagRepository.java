package com.skillbox.javapro21.repository;

import com.skillbox.javapro21.domain.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {

    Optional<Tag> findById(Long id);

    @Query("select t from Tag t where t.tag = :tag")
    Optional<Tag> findByTag(String tag);

    @Query("select t from Tag t ")
    Page<Tag> findTags(Pageable pageable);

    @Query("select t from Tag t " +
            "where LOWER(t.tag) like %:tag% " +
            "group by t.id " +
            "order by t.tag desc")
    Page<Tag> findAllByTag(String tag, Pageable pageable);

    @Query("select t from Tag t where t.tag = :name")
    Tag findByName(String name);
}
