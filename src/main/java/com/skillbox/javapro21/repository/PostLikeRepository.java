package com.skillbox.javapro21.repository;

import com.skillbox.javapro21.domain.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    List<PostLike> findPostLikeByPostId(Long id);

    Optional<PostLike> findByPersonIdAndPostId(Long personId, Long postId);

    @Query("select count(pl) from PostLike pl")
    Long findCountLikes();
}
