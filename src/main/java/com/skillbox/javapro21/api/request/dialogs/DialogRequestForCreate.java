package com.skillbox.javapro21.api.request.dialogs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DialogRequestForCreate {
    @JsonProperty("users_ids")
    private List<Long> usersIds;
}
