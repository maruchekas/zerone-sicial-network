package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.api.request.AuthRequest;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.exception.UserExistException;
import com.skillbox.javapro21.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Tag(name = "Контроллер авторизации")
@RequestMapping("/api/vi/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AccountService accountService;

    @Operation(summary = "вход")
    @PostMapping("/login")
    public ResponseEntity<DataResponse> login(@RequestParam String email,
                                              @RequestParam String password) throws UserExistException {

        // TODO запрос на вход (лог/пасс), проверка существования юзера,
        //  выброс исключения notSuchUserExistsException. Вернуть пользователя
        log.info("Login user with email {} and name {}", email, email);
        AuthRequest authRequest = new AuthRequest();

        return new ResponseEntity<>(accountService.login(authRequest), HttpStatus.OK);
    }

    @Operation(summary = "выход")
    @PostMapping("/logout")
    public ResponseEntity<DataResponse> logOut(@RequestBody AuthRequest authRequest) throws UserExistException {
        // TODO взять авторизованного пользователя, убить токен, завершить сессию
        log.info("User with email {} and name {} logout", authRequest.getEmail(), authRequest.getPassword());
        return new ResponseEntity<>(accountService.login(authRequest), HttpStatus.OK);
    }
}
