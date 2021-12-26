package com.skillbox.javapro21.repository;

import com.skillbox.javapro21.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

//    @Query("select p from Post p " +
//            "left join Person ps on ps.id = p.author.id " +
//            "join p.tags t " +
//            "where t.id in (:tags) " +
//            "and (p.time >= :dateFrom and p.time <= :dateTo) and p.time <= current_timestamp " +
//            "and (p.postText like '%'||:text||'%' or p.title like '%'||:text||'%') and :text != '' " +
//            "group by p.id " +
//            "order by p.time desc")
//    Page<Post> findPostsByTextContainingByDateExcludingBlockers(String text,
//                                                                LocalDateTime dateFrom,
//                                                                LocalDateTime dateTo,
//                                                                String author,
//                                                                String tags,
//                                                                Pageable pageable);
}
