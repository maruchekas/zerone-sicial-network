package com.skillbox.javapro21.repository;

import com.skillbox.javapro21.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    @Query("select p from Post p " +
            "left join Person ps on ps.id = p.author.id " +
            "join p.tags t " +
            "where t.id in (:tags) " +
            "and (p.time >= :dateFrom and p.time <= :dateTo) and p.time <= current_timestamp " +
            "and (p.author.firstName like :author||'%' or p.author.lastName like :author||'%' and :author != '' or :author = '' ) " +
            "and (p.postText like '%'||:text||'%' or p.title like '%'||:text||'%') and :text != '' " +
            "group by p.id " +
            "order by p.time desc")
    Page<Post> findPostsByTextContainingByDateExcludingBlockers(String text, LocalDate dateFrom,
                                                                LocalDate dateTo, String author,
                                                                List<Long> tags, Pageable pageable);

    @Query("select p from Post p " +
            "left join Person ps on ps.id = p.author.id " +
            "where p.author.id not in (:blockers) " +
            "and p.isBlocked = 0 " +
            "and ( p.time >= :datetimeFrom and p.time <= :datetimeTo ) and p.time <= CURRENT_TIMESTAMP " +
            "and ( ( p.postText like '%'||:text||'%' or p.title like '%'||:text||'%' ) and  :text != '' or :text = '' ) " +
            "and ( ( p.author.firstName like :author||'%' or p.author.lastName like :author||'%' ) and :author != '' ) " +
            "group by p.id " +
            "order by p.time desc")
    Page<Post> findPostsByTextContainingByDateExcludingBlockersWithoutTags(String text, LocalDate datetimeFrom,
                                                                           LocalDate datetimeTo, String author,
                                                                           List<Long> blockers, Pageable pageable);
}
