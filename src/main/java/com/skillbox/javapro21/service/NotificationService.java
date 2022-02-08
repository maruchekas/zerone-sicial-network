package com.skillbox.javapro21.service;

import com.skillbox.javapro21.api.response.ListDataResponse;

import java.security.Principal;

public interface NotificationService {
    ListDataResponse<?> getNotifications(int offset, int itemPerPage, Principal principal);
}
