package com.skillbox.javapro21.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ListDataResponse <T extends Content> {
    private String error;
    private Instant timestamp;
    private int total;
    private int offset;
    private int perPage;
    private List<T> data;
}
