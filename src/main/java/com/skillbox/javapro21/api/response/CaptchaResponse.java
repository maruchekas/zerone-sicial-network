package com.skillbox.javapro21.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CaptchaResponse {
    @JsonProperty(value = "secret_code")
    private String secretCode;
    private String image;
}
