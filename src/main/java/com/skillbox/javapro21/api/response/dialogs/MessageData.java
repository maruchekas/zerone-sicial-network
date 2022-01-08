package com.skillbox.javapro21.api.response.dialogs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillbox.javapro21.domain.enumeration.ReadStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageData {
    private Long id;
    private LocalDateTime time;
    @JsonProperty(value = "author_id")
    private Long authorId;
    @JsonProperty(value = "recipient_id")
    private Long recipientId;
    @JsonProperty(value = "message_text")
    private String messageText;
    @JsonProperty(value = "read_status")
    private ReadStatus readStatus;
}
