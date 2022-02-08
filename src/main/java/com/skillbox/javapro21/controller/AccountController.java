package com.skillbox.javapro21.controller;

import com.mailjet.client.errors.MailjetException;
import com.skillbox.javapro21.aop.LastActivity;
import com.skillbox.javapro21.api.request.account.*;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.MessageOkContent;
import com.skillbox.javapro21.api.response.account.NotificationSettingData;
import com.skillbox.javapro21.exception.CaptchaCodeException;
import com.skillbox.javapro21.exception.NotFoundException;
import com.skillbox.javapro21.exception.TokenConfirmationException;
import com.skillbox.javapro21.exception.UserExistException;
import com.skillbox.javapro21.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.security.Principal;

@Slf4j
@RestController
@Tag(name = "Контроллер для работы с аккаунтом")
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @Operation(summary = "Регистрация")
    @PostMapping("/register")
    public ResponseEntity<?> registration(@RequestBody RegisterRequest registerRequest) throws UserExistException, MailjetException, IOException, CaptchaCodeException {
        log.info("Вызван метод регистрации по почте {}", registerRequest.getEmail());
        return new ResponseEntity<>(accountService.registration(registerRequest), HttpStatus.OK);
    }

    @Operation(summary = "Подтверждение регистрации")
    @GetMapping("/register/complete")
    public ModelAndView verifyRegistration(@RequestParam String email,
                                           @RequestParam String code) throws TokenConfirmationException {
        return accountService.verifyRegistration(email, code);
    }

    @Operation(summary = "Отправка ссылки на почту для восстановления пароля")
    @PutMapping("/password/recovery")
    public ResponseEntity<String> recoveryPasswordMessage(@RequestBody RecoveryRequest recoveryRequest) throws MailjetException, IOException {
        return new ResponseEntity<>(accountService.recoveryPasswordMessage(recoveryRequest), HttpStatus.OK);
    }

    @Operation(summary = "Разрешение на восстановление пароля")
    @GetMapping("/password/send_recovery_massage")
    public ResponseEntity<String> verifyRecovery(@RequestParam String email,
                                                 @RequestParam String code) throws TokenConfirmationException {
        return new ResponseEntity<>(accountService.verifyRecovery(email, code), HttpStatus.OK);
    }

    @Operation(summary = "Восстановление пароля")
    @GetMapping("/password/recovery/complete")
    public ResponseEntity<String> recoveryPassword(@RequestParam String email,
                                                   @RequestParam String password) {
        return new ResponseEntity<>(accountService.recoveryPassword(email, password), HttpStatus.OK);
    }

    @Operation(summary = "Смена пароля", security = @SecurityRequirement(name = "jwt"))
    @PutMapping("/password/set")
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<DataResponse<MessageOkContent>> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        return new ResponseEntity<>(accountService.changePassword(changePasswordRequest), HttpStatus.OK);
    }

    @Operation(summary = "Смена email", security = @SecurityRequirement(name = "jwt"))
    @PutMapping("/email")
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<DataResponse<MessageOkContent>> changeEmail(@RequestBody ChangeEmailRequest changeEmailRequest,
                                                                      Principal principal) throws UserExistException {
        return new ResponseEntity<>(accountService.changeEmail(changeEmailRequest, principal), HttpStatus.OK);
    }

    @Operation(summary = "Редактирование настроек оповещения", security = @SecurityRequirement(name = "jwt"))
    @PutMapping("/notifications")
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<DataResponse<MessageOkContent>> changeNotifications(@RequestBody ChangeNotificationsRequest changeNotificationsRequest,
                                                                              Principal principal) throws NotFoundException {
        return new ResponseEntity<>(accountService.changeNotifications(changeNotificationsRequest, principal), HttpStatus.OK);
    }

    @Operation(summary = "Получение настроек оповещения", security = @SecurityRequirement(name = "jwt"))
    @GetMapping("/notifications")
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<ListDataResponse<NotificationSettingData>> getNotifications(Principal principal) {
        return new ResponseEntity<>(accountService.getNotifications(principal), HttpStatus.OK);
    }
}
