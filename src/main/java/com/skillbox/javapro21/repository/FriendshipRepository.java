package com.skillbox.javapro21.repository;

import com.skillbox.javapro21.domain.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Integer> {

    @Query("select f from Friendship f " +
            "left join FriendshipStatus fs on fs.id = f.id " +
            "where f.srcPerson.id = :src and f.dstPerson.id = :dst " +
            "or f.srcPerson.id = :dst and f.dstPerson.id = :src")
    Optional<Friendship> findFriendshipBySrcPersonAndDstPerson(Long src, Long dst);
}
