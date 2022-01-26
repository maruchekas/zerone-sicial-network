package com.skillbox.javapro21.repository;

import com.skillbox.javapro21.domain.PersonToDialog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonToDialogRepository extends JpaRepository<PersonToDialog, Integer> {
    @Query("select p2d from PersonToDialog p2d " +
            "left join Dialog d on p2d.dialogId = d.id " +
            "where p2d.personId = :id and d.title like %:query% " +
            "group by p2d.id " +
            "order by p2d.lastCheck desc")
    Page<PersonToDialog> findDialogsByPersonIdAndQuery(Long id, String query, Pageable pageable);

    @Query("select p2d from PersonToDialog p2d " +
            "where p2d.personId = :id " +
            "group by p2d.id " +
            "order by p2d.lastCheck desc")
    Page<PersonToDialog> findDialogsByPerson(Long id, Pageable pageable);

    @Query("select p2d from PersonToDialog p2d " +
            "left join Person p on p2d.personId = p.id " +
            "left join Dialog d on p2d.dialogId = d.id " +
            "where p2d.personId = :id " +
            "and d.isBlocked = 0 and p.isBlocked = 0 ")
    List<PersonToDialog> findDialogsByPersonId(Long id);

    @Query("select p2d from PersonToDialog p2d " +
            "where p2d.personId = :personId " +
            "and p2d.dialogId = :dialogId ")
    PersonToDialog findDialogByPersonIdAndDialogId(Long personId, int dialogId);

    @Query("select p2d from PersonToDialog p2d " +
            "where p2d.dialogId = :dialogId and p2d.personId = :id")
    PersonToDialog findP2DByDialogAndMessage(int dialogId, Long id);
}
