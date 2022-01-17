package com.skillbox.javapro21.api.request.dialogs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MessageTextRequest {
    @JsonProperty(value = "message_text")
    private String messageText;
}
