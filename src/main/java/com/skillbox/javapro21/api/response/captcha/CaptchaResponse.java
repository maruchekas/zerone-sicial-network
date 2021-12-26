package com.skillbox.javapro21.api.response.captcha;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillbox.javapro21.api.response.Content;
import lombok.Data;

@Data
public class CaptchaResponse implements Content {
    @JsonProperty(value = "secret_code")
    private String secretCode;
    private String image;
}
