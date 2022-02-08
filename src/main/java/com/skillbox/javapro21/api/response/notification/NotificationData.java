package com.skillbox.javapro21.api.response.notification;

import com.skillbox.javapro21.api.response.Content;
import lombok.Data;

@Data
public class NotificationData implements Content {

    private String type;
    private Long sentTime;
}
