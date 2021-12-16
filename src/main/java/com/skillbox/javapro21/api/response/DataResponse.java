package com.skillbox.javapro21.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

@lombok.Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataResponse <T extends Data> {
    private String error;
    private Instant timestamp;
    private T data;
}
