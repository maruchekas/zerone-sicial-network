package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.aop.LastActivity;
import com.skillbox.javapro21.api.request.auth.AuthRequest;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.MessageOkContent;
import com.skillbox.javapro21.api.response.account.AuthData;
import com.skillbox.javapro21.api.response.captcha.CaptchaResponse;
import com.skillbox.javapro21.exception.NotSuchUserOrWrongPasswordException;
import com.skillbox.javapro21.exception.UserLegalException;
import com.skillbox.javapro21.service.AuthService;
import com.skillbox.javapro21.service.CaptchaService;
import com.skillbox.javapro21.service.impl.CaptchaServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@RestController
@Tag(name = "Контроллер авторизации")
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;


    @Operation(summary = "Вход через логин/пароль")
    @PostMapping("/login")
    public ResponseEntity<DataResponse<AuthData>> login(@RequestBody AuthRequest authRequest)
            throws NotSuchUserOrWrongPasswordException, UserLegalException {
        log.info("Пользователь {} входит в приложение", authRequest.getEmail());
        return new ResponseEntity<>(authService.login(authRequest), HttpStatus.OK);
    }

    @Operation(summary = "Выход")
    @PostMapping("/logout")
    @LastActivity
    public ResponseEntity<DataResponse<MessageOkContent>> logOut(Principal principal) {

        String userEmail = principal != null ? principal.getName() : "";
        log.info("Пользователь {} вышел из приложения", userEmail);
        return new ResponseEntity<>(authService.logout(), HttpStatus.OK);
    }

}
