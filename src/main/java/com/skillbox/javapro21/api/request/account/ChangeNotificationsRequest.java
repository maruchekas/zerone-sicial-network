package com.skillbox.javapro21.api.request.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillbox.javapro21.domain.enumeration.NotificationType;
import lombok.Data;

@Data
public class ChangeNotificationsRequest {
    @JsonProperty("notification_type")
    private NotificationType notificationType;
    private boolean enable;
}
