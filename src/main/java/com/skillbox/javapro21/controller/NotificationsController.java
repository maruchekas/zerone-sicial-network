package com.skillbox.javapro21.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@Tag(name = "Контроллер для работы с оповещениями")
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationsController {


    @Operation(summary = "Получить список уведомлений для текущего пользователя")
    @GetMapping("")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<String> getNotifications(@RequestParam(name = "offset", defaultValue = "0") int offset,
                                                   @RequestParam(name = "item_per_page", defaultValue = "20") int itemPerPage,
                                                   Principal principal) {
//        log.info("Получение списка уведомлений ожидающих прочтения {}", principal.getName());
        return new ResponseEntity<>("Уведомления", HttpStatus.OK);
    }

    @Operation(summary = "Отметить уведомление как \"прочитанное\"")
    @PutMapping("")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<String> verifyRegistration(@RequestParam int id,
                                                     @RequestParam boolean all,
                                                     Principal principal) {
//        log.info("Can`t verify user with email {}", principal.getName());
        return new ResponseEntity<>("уведомления отмечены прочтенными", HttpStatus.OK);
    }

}
