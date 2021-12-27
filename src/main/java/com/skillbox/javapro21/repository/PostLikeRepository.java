package com.skillbox.javapro21.repository;

import com.skillbox.javapro21.domain.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Set<PostLike> findPostLikeByPostId(Long id);
}
