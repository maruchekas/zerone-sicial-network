package com.skillbox.javapro21.repository;

import com.skillbox.javapro21.domain.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationTypeRepository extends JpaRepository<NotificationType, Integer> {
    Optional<NotificationType> findNotificationTypeByPersonId(Long id);
}
