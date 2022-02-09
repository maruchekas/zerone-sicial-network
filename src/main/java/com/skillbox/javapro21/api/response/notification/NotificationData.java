package com.skillbox.javapro21.api.response.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillbox.javapro21.api.response.Content;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class NotificationData implements Content {
    private String type;
    @JsonProperty(value = "sent_time")
    private Long sentTime;
}
