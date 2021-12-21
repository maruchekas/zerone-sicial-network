package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.api.response.CaptchaResponse;
import com.skillbox.javapro21.service.serviceImpl.CaptchaServiceImpl;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Data
@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {
    private final CaptchaServiceImpl captchaService;

    @GetMapping("/captcha")
    private ResponseEntity<CaptchaResponse> getCaptcha() {
        return new ResponseEntity<>(captchaService.getNewCaptcha(), HttpStatus.OK);
    }
}
