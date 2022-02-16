package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.aop.LastActivity;
import com.skillbox.javapro21.api.response.Content;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.service.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@Tag(name = "Контроллер для работы с хранилищем")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class StorageController {
    private final ResourceService resourceService;

    @Operation(summary = "Загрузка аватара пользователя в хранилище сервиса", security = @SecurityRequirement(name = "jwt"))
    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping(value = "/storage", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @LastActivity
    public ResponseEntity<DataResponse<Content>> saveUserAvatar(@RequestParam("type") String type,
                                                                @RequestParam(value = "file", required = false) MultipartFile file,
                                                                Principal principal) throws IOException {
        DataResponse<Content> response = resourceService.saveFileInStorage(type, file, principal);
        return ResponseEntity.ok(response);
    }
}
