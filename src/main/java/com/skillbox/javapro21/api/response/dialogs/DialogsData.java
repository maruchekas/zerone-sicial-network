package com.skillbox.javapro21.api.response.dialogs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillbox.javapro21.api.response.Content;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DialogsData implements Content {
    private Long id;
    @JsonProperty(value = "unread_count")
    private int unreadCount;
    @JsonProperty(value = "last_message")
    private MessageData lastMessage;
}
