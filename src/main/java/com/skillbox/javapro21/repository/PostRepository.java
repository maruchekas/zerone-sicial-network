package com.skillbox.javapro21.repository;

import com.skillbox.javapro21.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    @Query("select p from Post p " +
            "left join Person ps on ps.id = p.author.id " +
            "where (p.time >= :dateFrom and p.time <= :dateTo) and p.time <= current_timestamp " +
            "and (p.postText like '%'||:text||'%' or p.title like '%'||:text||'%') and :text != '' " +
            "group by p.id " +
            "order by p.time desc")
    Page<Post> findPostsByTextContainingByDateExcludingBlockers(String text, LocalDateTime dateFrom, LocalDateTime dateTo, Pageable pageable);
}
