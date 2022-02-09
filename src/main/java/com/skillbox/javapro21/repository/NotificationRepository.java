package com.skillbox.javapro21.repository;

import com.skillbox.javapro21.domain.Notification;
import com.skillbox.javapro21.domain.enumeration.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    List<Notification> findAllByPerson_Id(long personId);
    List<Notification> findAllByNotificationTypeEquals(NotificationType notificationType);

}
