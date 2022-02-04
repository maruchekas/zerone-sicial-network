package com.skillbox.javapro21.api.request.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String passwd1;
    private String passwd2;
    private String firstName;
    private String lastName;
    private String captcha;
    @JsonProperty("captcha_secret")
    private String captchaSecret;
}

//        captcha: "vujomiyod"
//        captcha_secret: "QYYSrQFsqWAC"
//        email: "captchatest@mail.net"
//        firstName: "Ираклий"
//        lastName: "Мясков"
//        passwd1: "111111111"
//        passwd2: "111111111"
