package com.skillbox.javapro21.api.request.notification;

import lombok.Data;

@Data
public class ReadNotificationRequest {
    private long id;
    private boolean all;
}
