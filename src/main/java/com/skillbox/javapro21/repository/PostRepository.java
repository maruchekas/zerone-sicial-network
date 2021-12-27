package com.skillbox.javapro21.repository;

import com.skillbox.javapro21.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    @Query("select p from Post p " +
            "left join Person ps on ps.id = p.author.id " +
            "left join p.tags t " +
            "where t.id in (:tags) " +
            "and p.isBlocked = 0 " +
            "and ps.isBlocked = 0 " +
            "and p.time between :dateFrom and :dateTo and p.time < CURRENT_TIMESTAMP " +
            "and (p.title like '%'||:text||'%' or p.postText like '%'||:text||'%') " +
            "and (p.author.firstName like :author||'%' or p.author.lastName like :author||'%' and :author != '' or :author = '' ) " +
            "group by p.id " +
            "order by p.time desc")
    Page<Post> findPostsByTextByAuthorByTagsContainingByDateExcludingBlockers(String text, LocalDateTime dateFrom,
                                                                              LocalDateTime dateTo, String author,
                                                                              List<Long> tags, Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "left join Person pr on p.author.id = pr.id " +
            "where p.isBlocked = 0 " +
            "and pr.isBlocked = 0 " +
            "and p.time between :dateFrom and :dateTo and p.time < CURRENT_TIMESTAMP " +
            "and (p.title like '%'||:text||'%' or p.postText like '%'||:text||'%') " +
            "and (p.author.firstName like :author||'%' or p.author.lastName like :author||'%' and :author != '' or :author = '' ) " +
            "group by p.id " +
            "order by time desc")
    Page<Post> findPostsByTextByAuthorWithoutTagsContainingByDateExcludingBlockers(String text, LocalDateTime dateFrom,
                                                                                   LocalDateTime dateTo, String author,
                                                                                   Pageable pageable);
}
