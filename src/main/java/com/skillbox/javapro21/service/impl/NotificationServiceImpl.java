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

import static com.skillbox.javapro21.domain.enumeration.NotificationType.*;


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
        List<Person> activeUsers = personRepository.findAllByIsApprovedAndIsBlocked(1, 0);
        for (Person person : activeUsers) {
            List<Person> friends = personRepository.findAllPersonFriends(person.getId(), PageRequest.of(0, Integer.MAX_VALUE)).getContent();
            for (Person friend : friends) {
                LocalDateTime birthDay = person.getBirthDate();
                if (birthDay != null && now.getMonth().equals(birthDay.getMonth()) && now.getDayOfMonth() == birthDay.getDayOfMonth()) {
                    Notification notification = new Notification()
                            .setSentTime(utilsService.getLocalDateTimeZoneOffsetUtc())
                            .setNotificationType(FRIEND_BIRTHDAY)
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

    public void checkBirthdayFromOneAndCreateNotificationToAnotherInCase(Person one, Person another) {
        LocalDateTime now = utilsService.getLocalDateTimeZoneOffsetUtc();
        if (one.getBirthDate() != null) {
            if (now.getMonth().equals(one.getBirthDate().minusDays(1).getMonth()) && now.getDayOfMonth() == one.getBirthDate().getDayOfMonth()) {
                Notification notification = new Notification()
                        .setSentTime(utilsService.getLocalDateTimeZoneOffsetUtc())
                        .setNotificationType(FRIEND_BIRTHDAY)
                        .setPerson(another)
                        .setEntityId(one.getId())
                        .setContact("Contact");
                notificationRepository.save(notification);
            }
        }
    }


    private List<Content> convertNotificationsToNotificationData(List<Notification> notifications) {
        return notifications.stream()
                .map(n -> new NotificationData()
                        .setId(n.getPerson().getId())
                        .setSentTime(utilsService.getTimestampFromLocalDateTime(n.getSentTime()))
                        .setType(n.getNotificationType()))
                .collect(Collectors.toList());
    }

    private List<Notification> getNotificationListBySetting(UserNotificationSettings settings, Person person) {
        List<Notification> allByPerson = notificationRepository.findAllByPerson(person);
        return allByPerson.stream()
                .filter(n -> filter(settings.isPostComment(), n.getNotificationType(), POST_COMMENT))
                .filter(n -> filter(settings.isCommentComment(), n.getNotificationType(), COMMENT_COMMENT))
                .filter(n -> filter(settings.isFriendsRequest(), n.getNotificationType(), FRIEND_REQUEST))
                .filter(n -> filter(settings.isMessage(), n.getNotificationType(), MESSAGE))
                .filter(n -> filter(settings.isFriendsBirthday(), n.getNotificationType(), FRIEND_BIRTHDAY))
                .toList();
    }

    private boolean filter(boolean setting, NotificationType typeToCheck, NotificationType typToMatch) {
        if (!setting) {
            return !typeToCheck.equals(typToMatch);
        }
        return true;
    }
}

