package com.skillbox.javapro21.api.response.dialogs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.skillbox.javapro21.api.response.Content;
import com.skillbox.javapro21.api.response.View;
import com.skillbox.javapro21.api.response.account.AuthData;
import com.skillbox.javapro21.domain.enumeration.ReadStatus;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonView(View.Public.class)
public class MessageContent implements Content {
    private Long id;
    private long time;
    @JsonProperty(value = "author")
    private AuthData author;
    @JsonProperty(value = "recipient")
    private AuthData recipient;
    @JsonProperty(value = "message_text")
    private String messageText;
    @JsonView(View.Dialogs.class)
    @JsonProperty(value = "read_status")
    private ReadStatus readStatus;
}
