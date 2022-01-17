package com.skillbox.javapro21.api.response.dialogs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillbox.javapro21.api.response.Content;
import com.skillbox.javapro21.api.response.account.AuthData;
import com.skillbox.javapro21.domain.enumeration.ReadStatus;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageContent implements Content {
    private Long id;
    private long time;
    @JsonProperty(value = "author")
    private AuthData author;
    @JsonProperty(value = "recipient_id")
    private AuthData recipientId;
    @JsonProperty(value = "message_text")
    private String messageText;
    @JsonProperty(value = "read_status")
    private ReadStatus readStatus;
}
