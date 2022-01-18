package com.skillbox.javapro21.repository;

import com.skillbox.javapro21.domain.Post;
import com.skillbox.javapro21.domain.enumeration.FriendshipStatusType;
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
            "and (p.time between :dateFrom and :dateTo and p.time < CURRENT_TIMESTAMP) " +
            "and (LOWER(p.title) like %:text% or LOWER(p.postText) like %:text%) " +
            "and (LOWER(ps.firstName) like %:author% or LOWER(ps.lastName) like %:author%) " +
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
            "and (LOWER(p.title) like %:text% or LOWER(p.postText) like %:text%) " +
            "and (LOWER(ps.firstName) like %:author% or LOWER(ps.lastName) like %:author%) " +
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
            "and (ps.isBlocked = 0 and p.isBlocked = 0) and p.time <= CURRENT_TIMESTAMP " +
            "group by p.id " +
            "order by p.time desc")
    Page<Post> findPostsByPersonId(Long id, Pageable pageable);

    @Query("select p from Post p " +
            "left join Person ps on ps.id = p.author.id " +
            "left join PostComment pc on pc.post.id = p.id " +
            "where ps.id = :id " +
            "and ps.isBlocked = 0 " +
            "and p.isBlocked = 0 " +
            "group by p.id " +
            "order by p.time desc")
    Page<Post> findPostsByAuthorId(Long id, Pageable pageable);

    @Query("select p from Post p " +
            "join Person ps on ps.id = p.author.id " +
            "where ps.id in (:friendsAndSubscribersIds) " +
            "and p.isBlocked = 0 " +
            "and ps.isBlocked = 0 " +
            "and (LOWER(p.title) like %:text% or LOWER(p.postText) like %:text%) " +
            "group by p.id " +
            "order by p.time desc")
    Page<Post> findPostsByTextContainingNoBlocked(String text, List<Long> friendsAndSubscribersIds, Pageable pageable);


    @Query("SELECT p FROM Post p " +
            "left join Person ps on ps.id = p.author.id " +
            "where p.isBlocked = 0 " +
            "and ps.isBlocked = 0 " +
            "and (p.time between :dateFrom and :dateTo and p.time < CURRENT_TIMESTAMP) " +
            "and (LOWER(p.title) like %:text% or LOWER(p.postText) like %:text%) " +
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

    @Query("SELECT p FROM Post p " +
            "left join Person ps on ps.id = p.author.id " +
            "where p.isBlocked = 0 " +
            "and ps.isBlocked = 0 " +
            "and (p.time between :dateFrom and :dateTo and p.time < CURRENT_TIMESTAMP) " +
            "and (LOWER(ps.firstName) like %:author% or LOWER(ps.lastName) like %:author%) " +
            "group by p.id " +
            "order by p.time desc")
    Page<Post> findAllPostsByAuthor(String author, LocalDateTime dateFrom, LocalDateTime dateTo, Pageable pageable);


    @Query("select p from Post p " +
            "join Person ps on ps.id = p.author.id " +
            "where ps.id in (:friendsAndSubscribersIds) " +
            "and p.isBlocked = 0 " +
            "and ps.isBlocked = 0 " +
            "group by p.id " +
            "order by p.time desc")
    Page<Post> findPostsContainingNoBlocked(List<Long> friendsAndSubscribersIds, Pageable pageable);

    @Query("select p from Post p " +
            "join Person ps on ps.id = p.author.id " +
            "where p.author.id not in (:ids) " +
            "and p.isBlocked = 0 " +
            "and ps.isBlocked = 0 " +
            "order by p.likes.size desc")
    Page<Post> findBestPostsByPerson(List<Long> ids, Pageable pageable);
}
