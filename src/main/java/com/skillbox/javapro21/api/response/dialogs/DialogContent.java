package com.skillbox.javapro21.api.response.dialogs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillbox.javapro21.api.response.Content;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DialogContent implements Content {
    private int id;
    @JsonProperty(value = "unread_count")
    private long unreadCount;
    @JsonProperty(value = "last_message")
    private MessageContent lastMessage;
}
