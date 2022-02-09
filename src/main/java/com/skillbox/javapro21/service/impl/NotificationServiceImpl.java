package com.skillbox.javapro21.service.impl;

import com.skillbox.javapro21.api.response.Content;
import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.notification.NotificationData;
import com.skillbox.javapro21.domain.Notification;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.domain.UserNotificationSettings;
import com.skillbox.javapro21.domain.enumeration.NotificationType;
import com.skillbox.javapro21.repository.NotificationRepository;
import com.skillbox.javapro21.repository.PersonRepository;
import com.skillbox.javapro21.repository.UserNotificationSettingsRepository;
import com.skillbox.javapro21.service.NotificationService;
import io.swagger.models.auth.In;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final UtilsService utilsService;
    private final PersonRepository personRepository;
    private final UserNotificationSettingsRepository userNotificationSettingsRepository;

    /**
     * 1. Получить глобальные настройки оповещений пользователя.
     * 2. Заполнение Notifications относительно глобальных настроек.
     * 3. Получение информации и занесение в БД по каждой отдельной настройки.
     * 4. Сборка ответов в ListDataResponse.
     */


    @Override
    public ListDataResponse<Content> getNotifications(int offset, int itemPerPage, Principal principal) {
        Person person = utilsService.findPersonByEmail(principal.getName());
        Optional<UserNotificationSettings> possibleSettings =
                userNotificationSettingsRepository.findNotificationSettingsByPersonId(person.getId());
        if (possibleSettings.isEmpty()) {
            return utilsService.getListDataResponse(0, 0, 20, new ArrayList<>());
        }
        List<Notification> notifications = getNotificationListBySetting(possibleSettings.get(), person);

        return utilsService.getListDataResponse(notifications.size(), 0, 20, convertNotificationsToNotificationData(notifications));
    }


    private List<Content> convertNotificationsToNotificationData(List<Notification> notifications) {
        return notifications.stream()
                .map(n -> new NotificationData()
                            .setSentTime(utilsService.getTimestampFromLocalDateTime(n.getSentTime()))
                            .setType(n.getNotificationType()))
                .collect(Collectors.toList());
    }

    private List<Notification> getNotificationListBySetting(UserNotificationSettings settings, Person person) {
        List<Notification> result = new ArrayList<>();

        if (settings.isPostComment()) {
            result.addAll(notificationRepository.findAllByNotificationTypeEquals(NotificationType.POST_COMMENT));
        }
        if (settings.isCommentComment()) {
            result.addAll(notificationRepository.findAllByNotificationTypeEquals(NotificationType.COMMENT_COMMENT));
        }
        if (settings.isFriendsRequest()) {
            result.addAll(notificationRepository.findAllByNotificationTypeEquals(NotificationType.FRIEND_REQUEST));
        }
        if (settings.isMessage()) {
            result.addAll(notificationRepository.findAllByNotificationTypeEquals(NotificationType.MESSAGE));
        }
        if (settings.isFriendsBirthday()) {
            result.addAll(getFriendsBirthdayNotifications(person));
        }
        return result;
    }
    private NotificationData convertNotificationToNotificationData(Notification notification) {
        NotificationData notificationData = new NotificationData();
        notification.setNotificationType(notification.getNotificationType());
        notification.setSentTime(notification.getSentTime());
        return notificationData;
    }

    private List<Notification> getFriendsBirthdayNotifications(Person person) {
        List<Person> friends = personRepository.findAllPersonFriends(person.getId(), PageRequest.of(0, Integer.MAX_VALUE)).getContent();
        List<Notification> friendsWithBirthday = friends.stream()
                .filter(f -> {
                    LocalDateTime now = utilsService.getLocalDateTimeZoneOffsetUtc();
                    LocalDateTime birthDay = f.getBirthDate();
                    return (now.getMonth().equals(birthDay.getMonth()) && now.getDayOfMonth() == birthDay.getDayOfMonth());})
                .map(f -> new Notification()
                            .setSentTime(utilsService.getLocalDateTimeZoneOffsetUtc())
                            .setNotificationType(NotificationType.FRIEND_BIRTHDAY)
                            .setPerson(person)
                            .setEntityId(f.getId())
                            .setContact("Contact"))
                .toList();
        return friendsWithBirthday;
    private List<NotificationData> convertAll(List<Notification> notifications) {
        List<NotificationData> notificationDataList = new ArrayList<>();
        for(Notification notification : notifications){
            notification.setNotificationType(notification.getNotificationType());
            notification.setSentTime(notification.getSentTime());
        } return notificationDataList;
    }
}

