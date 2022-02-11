package com.skillbox.javapro21.service.impl;

import com.skillbox.javapro21.api.request.notification.ReadNotificationRequest;
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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Component
@EnableScheduling
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UtilsService utilsService;
    private final PersonRepository personRepository;
    private final UserNotificationSettingsRepository userNotificationSettingsRepository;


    @PostConstruct
    void beforeAll() {
        utilsService.setNotificationService(this);
        checkBirthdayNotifications();
    }

    @Scheduled(cron = "5 0 0 * * *")
    void checkBirthdayNotifications() {
        LocalDateTime now = utilsService.getLocalDateTimeZoneOffsetUtc();
        List<Person> allUsers = personRepository.findAll();
        for (Person person : allUsers) {
            List<Person> friends = personRepository.findAllPersonFriends(person.getId(), PageRequest.of(0, Integer.MAX_VALUE)).getContent();
            for (Person friend : friends) {
                LocalDateTime birthDay = person.getBirthDate();
                if (now.getMonth().equals(birthDay.getMonth()) && now.getDayOfMonth() == birthDay.getDayOfMonth()) {
                    Notification notification = new Notification()
                                                .setSentTime(utilsService.getLocalDateTimeZoneOffsetUtc())
                                                .setNotificationType(NotificationType.FRIEND_BIRTHDAY)
                                                .setPerson(person)
                                                .setEntityId(friend.getId())
                                                .setContact("Contact");
                    notificationRepository.save(notification);
                }
            }
        }
    }


    @Override
    public ListDataResponse<Content> getNotifications(int offset, int itemPerPage, Principal principal) {
        Person person = utilsService.findPersonByEmail(principal.getName());
        Optional<UserNotificationSettings> possibleSettings =
                userNotificationSettingsRepository.findNotificationSettingsByPersonId(person.getId());
        if (possibleSettings.isEmpty()) {
            return utilsService.getListDataResponse(0, offset, itemPerPage, new ArrayList<>());
        }
        List<Notification> notifications = getNotificationListBySetting(possibleSettings.get(), person);

        return utilsService.getListDataResponse(notifications.size(), offset, itemPerPage, convertNotificationsToNotificationData(notifications));
    }

    @Transactional
    @Override
    public ListDataResponse<Content> readNotification(ReadNotificationRequest request, Principal principal) {
        Person person = utilsService.findPersonByEmail(principal.getName());
        if (request.isAll()) {
            notificationRepository.deleteAll(notificationRepository.findAllByPerson(person));
            return getNotifications(0, 20, principal);
        }
        notificationRepository.deleteById(request.getId());
        return getNotifications(0, 20, principal);
    }


    private List<Content> convertNotificationsToNotificationData(List<Notification> notifications) {
        return notifications.stream()
                .map(n -> new NotificationData()
                            .setId(n.getId())
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
            result.addAll(notificationRepository.findAllByNotificationTypeEquals(NotificationType.FRIEND_BIRTHDAY));
        }
        return result;
    }

    public void checkBirthdayFromOneAndCreateNotificationToAnotherInCase(Person one, Person another) {
        LocalDateTime now = utilsService.getLocalDateTimeZoneOffsetUtc();
        if (now.getMonth().equals(one.getBirthDate().minusDays(1).getMonth()) && now.getDayOfMonth() == one.getBirthDate().getDayOfMonth()) {
            Notification notification = new Notification()
                                            .setSentTime(utilsService.getLocalDateTimeZoneOffsetUtc())
                                            .setNotificationType(NotificationType.FRIEND_BIRTHDAY)
                                            .setPerson(another)
                                            .setEntityId(one.getId())
                                            .setContact("Contact");
            notificationRepository.save(notification);
        }
    }
}

