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
            "and m.messageText like %:query% " +
            "and m.isBlocked = 0 " +
            "group by m.id " +
            "order by m.time desc")
    Page<Message> findAllMessagesByPersonIdAndQuery(Long id, String query, Pageable pageable);

    @Query("select m from Message m " +
            "left join Dialog d on d.id = m.dialog.id " +
            "left join PersonToDialog p2d on p2d.dialog.id = d.id " +
            "where m.dialog.id = :id and p2d.person.id = :personId " +
            "and m.isBlocked = 0 " +
            "order by m.time asc")
    Page<Message> findByDialogIdAndPersonId(int id, Long personId, Pageable pageable);

    @Query("select m from Message m " +
            "left join Dialog d on d.id = m.dialog.id " +
            "left join PersonToDialog p2d on p2d.dialog.id = d.id " +
            "where m.dialog.id = :id and p2d.person.id = :personId " +
            "and m.isBlocked = 0 " +
            "and m.messageText like %:query% " +
            "order by m.time asc")
    Page<Message> findByDialogIdAndPersonIdAndQuery(int id, Long personId, String query, Pageable pageable);

    @Query("select m from Message m " +
            "left join Dialog d on d.id = m.dialog.id " +
            "left join PersonToDialog p2d on p2d.dialog.id = d.id " +
            "where m.dialog.id = :id and p2d.person.id = :personId " +
            "and m.isBlocked = 0 " +
            "group by m.id having m.id > :fromMessageId " +
            "order by m.time asc")
    Page<Message> findByDialogIdAndPersonIdAndMessageId(int id, Long personId, int fromMessageId, Pageable pageable);

    @Query("select m from Message m " +
            "left join Dialog d on d.id = m.dialog.id " +
            "left join PersonToDialog p2d on p2d.dialog.id = d.id " +
            "where m.dialog.id = :id and p2d.person.id = :personId " +
            "and m.messageText like %:query% " +
            "and m.isBlocked = 0 " +
            "group by m.id having m.id >= :fromMessageId " +
            "order by m.time asc")
    Page<Message> findByDialogIdAndPersonIdAndQueryAndMessageId(int id, Long personId, String query, int fromMessageId, Pageable pageable);

    @Query("select m from Message m " +
            "where m.dialog.id = :dialogId and m.id = :messageId " +
            "and m.isBlocked = 0 ")
    Message findByDialogIdMessageId(int dialogId, Long messageId);

    @Query("select m from Message m " +
            "where m.dialog.id = :dialogId and m.id = :messageId " +
            "and m.isBlocked = 2 ")
    Message findDeletedMessageByDialogIdMessageId(int dialogId, Long messageId);
}
