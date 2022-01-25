package com.skillbox.javapro21.api.response.friends;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillbox.javapro21.api.response.Content;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class StatusContent implements Content {
    @JsonProperty("user_id")
    private Long userId;
    private String status;
}
