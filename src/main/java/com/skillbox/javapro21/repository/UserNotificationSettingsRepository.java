package com.skillbox.javapro21.repository;

import com.skillbox.javapro21.domain.UserNotificationSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserNotificationSettingsRepository extends JpaRepository<UserNotificationSettings, Integer> {
    @Query("select distinct uss from UserNotificationSettings uss where uss.person.id = :person_id ")
    Optional<UserNotificationSettings> findNotificationSettingsByPersonId(Long person_id);
}
