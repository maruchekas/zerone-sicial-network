package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.api.request.AuthRequest;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.account.AuthContent;
import com.skillbox.javapro21.exception.NotSuchUserOrWrongPasswordException;
import com.skillbox.javapro21.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Контроллер авторизации")
@RequestMapping("/api/vi/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Вход через логин/пароль")
    @PostMapping("/login")
    public ResponseEntity<DataResponse<AuthContent>> login(@RequestBody AuthRequest authRequest) throws NotSuchUserOrWrongPasswordException {

        // TODO запрос на вход (лог/пасс), проверка существования юзера,
        //  выброс исключения notSuchUserExistsException. Вернуть пользователя
        log.info("Login user with email {}", authRequest.getEmail());

        return new ResponseEntity<>(authService.login(authRequest), HttpStatus.OK);
    }

    @Operation(summary = "Выход")
    @PostMapping("/logout")
    public ResponseEntity<DataResponse<AuthContent>> logOut() {
        log.info("User has logout");

        return new ResponseEntity<>(authService.logout(), HttpStatus.OK);
    }
}
