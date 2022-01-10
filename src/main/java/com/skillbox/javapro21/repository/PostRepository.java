package com.skillbox.javapro21.repository;

import com.skillbox.javapro21.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    @Query("select p from Post p " +
            "where p.isBlocked = 0 and p.id = :id")
    Optional<Post> findPostById(Long id);

    @Query("select p from Post p " +
            "join Person ps on ps.id = p.author.id " +
            "join PostToTag pst on pst.postId = p.id " +
            "where pst.tagId in (:tags) " +
            "and p.isBlocked = 0 " +
            "and ps.isBlocked = 0 " +
            "and p.time between :dateFrom and :dateTo and p.time < CURRENT_TIMESTAMP " +
            "or ((p.title like '%'||:text||'%' or p.postText like '%'||:text||'%') " +
            "or (ps.firstName like :author||'%' or ps.lastName like :author||'%' and :author != '' or :author = '' )) " +
            "group by p.id " +
            "order by p.time desc")
    Page<Post> findPostsByTextByAuthorByTagsContainingByDateExcludingBlockers(String text, LocalDateTime dateFrom,
                                                                              LocalDateTime dateTo, String author,
                                                                              List<Long> tags, Pageable pageable);

    @Query("SELECT p FROM Post p " +
            "left join Person ps on ps.id = p.author.id " +
            "where p.isBlocked = 0 " +
            "and ps.isBlocked = 0 " +
            "and (p.time between :dateFrom and :dateTo and p.time < CURRENT_TIMESTAMP) " +
            "and (p.title like %:text% or p.postText like %:text%) " +
            "and (ps.firstName like :author% or ps.lastName like :author% ) " +
            "group by p.id " +
            "order by p.time desc")
    Page<Post> findPostsByTextByAuthorWithoutTagsContainingByDateExcludingBlockers(String text, LocalDateTime dateFrom,
                                                                                   LocalDateTime dateTo, String author,
                                                                                   Pageable pageable);
    @Query("select p from Post p " +
            "where p.id = :id")
    Optional<Post> findDeletedPostById(Long id);

    @Query("select p from Post p " +
            "left join Person ps on ps.id = p.author.id " +
            "left join PostComment pc on pc.post.id = p.id " +
            "where ps.id = :id " +
            "and (ps.isBlocked = 0 and p.isBlocked = 0) and p.time < CURRENT_TIMESTAMP " +
            "group by p.id " +
            "order by p.time desc")
    Page<Post> findPostsByPersonId(Long id, Pageable pageable);

    @Query("select p from Post p " +
            "join Person ps on ps.id = p.author.id " +
            "where ps.id in (:friendsAndSubscribersIds) " +
            "and p.isBlocked = 0 " +
            "and ps.isBlocked = 0 " +
            "and (p.title like %:text% or p.postText like %:text%) " +
            "group by p.id " +
            "order by p.time asc")
    Page<Post> findPostsByTextExcludingBlockers(String text, List<Long> friendsAndSubscribersIds, Pageable pageable);


    @Query("SELECT p FROM Post p " +
            "left join Person ps on ps.id = p.author.id " +
            "where p.isBlocked = 0 " +
            "and ps.isBlocked = 0 " +
            "and (p.time between :dateFrom and :dateTo and p.time < CURRENT_TIMESTAMP) " +
            "and (p.title like %:text% or p.postText like %:text%) " +
            "group by p.id " +
            "order by p.time desc")
    Page<Post> findAllPostsByText(String text, LocalDateTime dateFrom, LocalDateTime dateTo, Pageable pageable);

    @Query("SELECT p FROM Post p " +
            "left join Person ps on ps.id = p.author.id " +
            "where p.isBlocked = 0 " +
            "and ps.isBlocked = 0 " +
            "and (p.time between :dateFrom and :dateTo and p.time < CURRENT_TIMESTAMP) " +
            "group by p.id " +
            "order by p.time desc")
    Page<Post> findAllPosts(LocalDateTime dateFrom, LocalDateTime dateTo, Pageable pageable);
}
