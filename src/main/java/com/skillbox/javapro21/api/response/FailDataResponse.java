package com.skillbox.javapro21.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FailDataResponse implements Content{

    private String error;
    @JsonProperty("error_description")
    private String description;
}
