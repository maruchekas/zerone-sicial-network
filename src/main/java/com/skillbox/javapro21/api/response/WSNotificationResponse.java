package com.skillbox.javapro21.api.response;

import com.skillbox.javapro21.domain.enumeration.NotificationType;
import lombok.Data;

@Data
public class WSNotificationResponse {
    NotificationType notificationType;
    String initiatorName;
}
