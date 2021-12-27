package com.skillbox.javapro21.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataResponse<T extends Content> {
    private String error;
    @JsonProperty("error_description")
    private String errorDescription;
    private LocalDateTime timestamp;
    private T data;
}
