package com.skillbox.javapro21.api.response.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillbox.javapro21.api.response.Content;
import com.skillbox.javapro21.domain.enumeration.NotificationTypeStatus;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class NotificationSettingData implements Content {
    @JsonProperty(value = "notification_type")
    private NotificationTypeStatus notificationTypeStatus;
    boolean enable;
}
