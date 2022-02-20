package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.aop.LastActivity;
import com.skillbox.javapro21.api.request.notification.ReadNotificationRequest;
import com.skillbox.javapro21.api.response.Content;
import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "Контроллер для работы с оповещениями")
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationsController {

    private final NotificationService notificationService;


    @Operation(summary = "Получить список уведомлений для текущего пользователя", security = @SecurityRequirement(name = "jwt"))
    @GetMapping("")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ListDataResponse<Content>> getNotifications(@RequestParam(name = "offset", defaultValue = "0") int offset,
                                                                      @RequestParam(name = "item_per_page", defaultValue = "20") int itemPerPage,
                                                                      Principal principal) {
        return new ResponseEntity<>(notificationService.getNotifications(offset, itemPerPage, principal), HttpStatus.OK);
    }

    @Operation(summary = "Отметить уведомление как прочитанное", security = @SecurityRequirement(name = "jwt"))
    @PutMapping("")
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<ListDataResponse<Content>> readNotification(@RequestBody ReadNotificationRequest request,
                                                                      Principal principal) {
        return new ResponseEntity<>(notificationService.readNotification(request, principal), HttpStatus.OK);
    }
}
