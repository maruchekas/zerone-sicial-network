package com.skillbox.javapro21.repository;

import com.skillbox.javapro21.domain.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    @Query("select m from Message m " +
            "where m.author.id = :id " +
            "and (m.messageText like '%'||:query||'%') " +
            "group by m.id " +
            "order by m.time desc")
    Page<Message> findAllMessagesByPersonIdAndQuery(Long id, String query, Pageable pageable);
}
