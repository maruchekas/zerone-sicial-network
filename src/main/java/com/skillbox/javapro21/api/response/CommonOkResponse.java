package com.skillbox.javapro21.api.response;

import lombok.Data;

@Data
public class CommonOkResponse {

    private String error;
    private Long timestamp;
    private Object data;

}