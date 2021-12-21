package com.skillbox.javapro21.service;

import com.skillbox.javapro21.api.response.captcha.CaptchaResponse;
import org.springframework.stereotype.Service;

@Service
public interface CaptchaService {
    CaptchaResponse getNewCaptcha();
}
