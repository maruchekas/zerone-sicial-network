package com.skillbox.javapro21.api.response.notification;

import com.skillbox.javapro21.api.response.Content;
import com.skillbox.javapro21.domain.enumeration.NotificationType;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class NotificationData implements Content {

    private Long id;
    private NotificationType type;
    private Long sentTime;
}
