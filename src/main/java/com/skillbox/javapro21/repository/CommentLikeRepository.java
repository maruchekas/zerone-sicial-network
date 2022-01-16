package com.skillbox.javapro21.repository;

import com.skillbox.javapro21.domain.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByPersonIdAndCommentId(Long personId, Long commentId);
    List<CommentLike> findAllByCommentId(Long postId);
}
