package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.api.request.RegisterRequest;
import com.skillbox.javapro21.api.response.AccountResponse;
import com.skillbox.javapro21.exception.UserExistException;
import com.skillbox.javapro21.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@Slf4j
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
    public ResponseEntity<AccountResponse> registration(@RequestBody RegisterRequest registerRequest) throws UserExistException {
        return new ResponseEntity<>(accountService.registration(registerRequest), HttpStatus.OK);
    }
}
