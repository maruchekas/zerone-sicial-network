package com.skillbox.javapro21.service;

import com.skillbox.javapro21.api.response.Content;
import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.notification.NotificationData;
import com.skillbox.javapro21.domain.Notification;

import java.security.Principal;

public interface NotificationService {
    ListDataResponse<Content> getNotifications(int offset, int itemPerPage, Principal principal);
}
