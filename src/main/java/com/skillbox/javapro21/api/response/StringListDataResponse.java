package com.skillbox.javapro21.api.response;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Accessors(chain = true)
@Data
public class StringListDataResponse {

    private String error;
    private long timestamp;
    private List<String> data;
}
