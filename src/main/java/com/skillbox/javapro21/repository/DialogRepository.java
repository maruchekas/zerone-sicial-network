package com.skillbox.javapro21.repository;

import com.skillbox.javapro21.domain.Dialog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DialogRepository extends JpaRepository<Dialog, Integer> {
    @Query("SELECT d " +
            "FROM Dialog d " +
            "left join PersonToDialog p2d on p2d.dialog.id = d.id " +
            "WHERE p2d.person.id = :id and d.persons = :idDsc " +
            "AND d.isBlocked = 0 ")
    Dialog findPersonToDialogByPersonDialog(Long id, Long idDsc);
}
