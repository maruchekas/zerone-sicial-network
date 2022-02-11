package com.skillbox.javapro21.repository;

import com.skillbox.javapro21.domain.Notification;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.domain.enumeration.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findAllByPerson(Person person);

    List<Notification> findAllByNotificationTypeEquals(NotificationType notificationType);

    void deleteById(long id);
}
