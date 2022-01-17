package com.skillbox.javapro21.api.response.dialogs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillbox.javapro21.api.response.Content;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class DialogPersonIdContent implements Content {
    @JsonProperty("user_ids")
    private List<Long> userIds;
}
