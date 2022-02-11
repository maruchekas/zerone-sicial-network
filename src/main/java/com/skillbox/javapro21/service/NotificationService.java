package com.skillbox.javapro21.service;

import com.skillbox.javapro21.api.request.notification.ReadNotificationRequest;
import com.skillbox.javapro21.api.response.Content;
import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.domain.Person;

import java.security.Principal;

public interface NotificationService {

    ListDataResponse<Content> getNotifications(int offset, int itemPerPage, Principal principal);
    ListDataResponse<Content> readNotification(ReadNotificationRequest request, Principal principal);
    void checkBirthdayFromOneAndCreateNotificationToAnotherInCase(Person one, Person another);
}
