package com.skillbox.javapro21.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonView(View.Public.class)
public class DataResponse<T extends Content> {
    private String error;
    private Long timestamp;
    private HttpStatus httpStatus;
    private T data;
}
