package com.skillbox.javapro21.api.request.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillbox.javapro21.domain.enumeration.NotificationTypeStatus;
import lombok.Data;

@Data
public class ChangeNotificationsRequest {
    @JsonProperty("notification_type")
    private NotificationTypeStatus notificationTypeStatus;
    private boolean enable;
}
