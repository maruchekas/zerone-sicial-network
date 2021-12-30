package com.skillbox.javapro21.repository;

import com.skillbox.javapro21.domain.FriendshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendshipStatusRepository extends JpaRepository<FriendshipStatus, Integer> {

    @Query("select fs from FriendshipStatus  fs " +
            "left join Friendship f on f.friendshipStatus.id = fs.id " +
            "left join Person p on p.id = f.srcPerson.id " +
            "where p.id = :id")
    FriendshipStatus findFriendshipStatusByPersonId(Long id);
}
