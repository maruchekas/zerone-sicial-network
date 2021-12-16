package com.skillbox.javapro21.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.Instant;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataResponse <T extends Content> {
    private String error;
    private Instant timestamp;
    private T data;
}
