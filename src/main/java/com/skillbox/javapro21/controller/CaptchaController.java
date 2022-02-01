package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.api.response.captcha.CaptchaResponse;
import com.skillbox.javapro21.service.CaptchaService;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Data
@RestController
@RequestMapping("/api/v1/auth")
public class CaptchaController {

    private final CaptchaService captchaService;

    @GetMapping("/captcha")
    private ResponseEntity<CaptchaResponse> getCaptcha() {
        return new ResponseEntity<>(captchaService.getNewCaptcha(), HttpStatus.OK);
    }

}
