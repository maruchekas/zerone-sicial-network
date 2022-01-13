package com.skillbox.javapro21.repository;

import com.skillbox.javapro21.domain.Language;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Long> {

    @Query("select l from Language l where l.info = :info group by l.id ")
    Page<Language> findAllByInfo(String info, Pageable pageable);

    @NonNull Page<Language> findAll(@NonNull Pageable pageable);
}
