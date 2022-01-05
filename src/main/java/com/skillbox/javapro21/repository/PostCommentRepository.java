package com.skillbox.javapro21.repository;

import com.skillbox.javapro21.domain.PostComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Integer> {
    @Query("select pc from PostComment pc " +
            "left join Post p on pc.post.id = p.id " +
            "where p.id = :id and pc.isBlocked = 0 and p.isBlocked = 0 " +
            "order by pc.time")
    Page<PostComment> findPostCommentsByPostId(Long id, Pageable pageable);

    @Query("select pc from PostComment pc " +
            "where pc.id = :id and pc.isBlocked = 0 ")
    Optional<PostComment> findById(Long id);

    @Query("select pc from PostComment pc " +
            "left join Post p on pc.post.id = p.id " +
            "where ( p.id = :parentId and pc.id = :id ) and ( pc.isBlocked = 0 and p.isBlocked = 0 )")
    Optional<PostComment> findPostCommentByIdAndPostId(Long id, Long parentId);

    @Query("select pc from PostComment pc " +
            "left join Post p on pc.post.id = p.id " +
            "where ( p.id = :parentId and pc.id = :id ) and ( pc.isBlocked = 2 and p.isBlocked = 0 )")
    Optional<PostComment> findPostCommentByIdAndParentIdWhichIsDelete(Long id, Long parentId);
}
