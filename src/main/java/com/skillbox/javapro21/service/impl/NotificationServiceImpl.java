package com.skillbox.javapro21.service.impl;

import com.skillbox.javapro21.api.response.Content;
import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.notification.NotificationData;
import com.skillbox.javapro21.domain.Notification;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.repository.NotificationRepository;
import com.skillbox.javapro21.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;


@Component
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final UtilsService utilsService;

    /**
     * 1. Получить глобальные настройки оповещений пользователя.
     * 2. Заполнение Notifications относительно глобальных настроек.
     * 3. Получение информации и занесение в БД по каждой отдельной настройки.
     * 4. Сборка ответов в ListDataResponse.
     */


    @Override
    public ListDataResponse<Content> getNotifications(int offset, int itemPerPage, Principal principal) {
        Person person = utilsService.findPersonByEmail(principal.getName());
        List<Notification> list = notificationRepository.findAllByPerson_Id(person.getId());

//        List<NotificationData>
        return null;
    }


    private NotificationData convertNotificationToNotificationData() {

        return null;
    }

}

