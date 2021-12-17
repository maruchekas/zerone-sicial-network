package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.api.request.RecoveryRequest;
import com.skillbox.javapro21.api.request.RegisterRequest;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.exception.TokenConfirmationException;
import com.skillbox.javapro21.exception.UserExistException;
import com.skillbox.javapro21.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Tag(name = "Контроллер для работы с аккаунтом")
@RequestMapping("/api/vi/account")
public class AccountController {
    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(summary = "регистрация")
    @PostMapping("/register")
    public ResponseEntity<DataResponse> registration(@RequestBody RegisterRequest registerRequest) throws UserExistException {
        log.info("Can`t create user with email {} and name {}", registerRequest.getEmail(), registerRequest.getFirstName());
        return new ResponseEntity<>(accountService.registration(registerRequest), HttpStatus.OK);
    }

    @Operation(summary = "подтверждение регистрации")
    @GetMapping("/register/complete")
    public ResponseEntity<String> verifyRegistration(@RequestParam String email,
                                                     @RequestParam String code) throws TokenConfirmationException {
        log.info("Can`t verify user with email {}", email);
        return new ResponseEntity<>(accountService.verifyRegistration(email, code), HttpStatus.OK);
    }

    @Operation(summary = "восстановление пароля")
    @PutMapping("/password/recovery")
    public ResponseEntity<String> recovery(@RequestBody RecoveryRequest recoveryRequest) {
        return new ResponseEntity<>(accountService.recovery(recoveryRequest), HttpStatus.OK);
    }

    @Operation(summary = "восстановление пароля")
    @PutMapping("/password/recovery/complete")
    public ResponseEntity<String> verifyRecovery(@RequestParam String email,
                                                 @RequestParam String code) throws TokenConfirmationException {
        return new ResponseEntity<>(accountService.verifyRecovery(email, code), HttpStatus.OK);
    }
}
