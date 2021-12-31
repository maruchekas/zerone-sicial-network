package com.skillbox.javapro21.repository;

import com.skillbox.javapro21.domain.PostComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Integer> {

    @Query("select pc from PostComment pc " +
            "left join Post p on pc.post.id = p.id " +
            "where p.id = :id " +
            "order by pc.time")
    Page<PostComment> findPostCommentsByPotId(Long id, Pageable pageable);
}
