package com.skillbox.javapro21.api.response.dialogs;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillbox.javapro21.api.response.Content;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LastActivityContent implements Content {
    private boolean online;
    @JsonProperty(value = "last_activity")
    private long lastActivity;
}
