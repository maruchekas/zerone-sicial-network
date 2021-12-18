package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.api.request.*;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.exception.TokenConfirmationException;
import com.skillbox.javapro21.exception.UserExistException;
import com.skillbox.javapro21.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

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
    public ResponseEntity<DataResponse<?>> registration(@RequestBody RegisterRequest registerRequest) throws UserExistException {
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

    @Operation(summary = "отправка ссылки на почту для восстановления пароля")
    @GetMapping("/password/send_recovery_massage")
    public ResponseEntity<String> recovery(@RequestBody RecoveryRequest recoveryRequest) {
        log.info("Not found user with email {}", recoveryRequest.getEmail());
        return new ResponseEntity<>(accountService.recovery(recoveryRequest), HttpStatus.OK);
    }

    @Operation(summary = "разрешение на восстановление пароля")
    @GetMapping("/password/recovery/complete")
    public ResponseEntity<String> verifyRecovery(@RequestParam String email,
                                                 @RequestParam String code) throws TokenConfirmationException {
        log.info("Can`t verify user with email {}", email);
        return new ResponseEntity<>(accountService.verifyRecovery(email, code), HttpStatus.OK);
    }

    @Operation(summary = "восстановление пароля")
    @PutMapping("/password/recovery")
    public ResponseEntity<String> recoveryPassword(@RequestParam String email,
                                                   @RequestParam String password) {
        log.info("Can`t verify user with email {}", email);
        return new ResponseEntity<>(accountService.recoveryPassword(email, password), HttpStatus.OK);
    }

    @Operation(summary = "смена пароля", security = @SecurityRequirement(name = "jwt"))
    @PutMapping("/password/set")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<DataResponse<?>> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        return new ResponseEntity<>(accountService.changePassword(changePasswordRequest), HttpStatus.OK);
    }

    @Operation(summary = "смена email", security = @SecurityRequirement(name = "jwt"))
    @PutMapping("/email")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<DataResponse<?>> changeEmail(@RequestBody ChangeEmailRequest changeEmailRequest,
                                                       Principal principal) {
        return new ResponseEntity<>(accountService.changeEmail(changeEmailRequest, principal), HttpStatus.OK);
    }

    @Operation(summary = "редактирование настроек оповещения", security = @SecurityRequirement(name = "jwt"))
    @PutMapping("/notifications")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<DataResponse<?>> changeNotifications(@RequestBody ChangeNotificationsRequest changeNotificationsRequest,
                                                       Principal principal) {
        return new ResponseEntity<>(accountService.changeNotifications(changeNotificationsRequest, principal), HttpStatus.OK);
    }
}
