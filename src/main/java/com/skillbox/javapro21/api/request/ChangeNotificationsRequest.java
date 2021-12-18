package com.skillbox.javapro21.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillbox.javapro21.domain.NotificationType;
import lombok.Data;

@Data
public class ChangeNotificationsRequest {
    @JsonProperty("notification_type")
    private NotificationType notificationType;
    private boolean enable;
}
