package com.skillbox.javapro21.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ListDataResponse<T extends Content> {
    private String error;
    private LocalDateTime timestamp;
    private int total;
    private int offset;
    private int perPage;
    private List<T> data;
}
