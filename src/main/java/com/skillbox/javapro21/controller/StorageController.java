package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.api.request.profile.ChangeAvatarRequest;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.account.AvatarUploadData;
import com.skillbox.javapro21.service.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;

@Slf4j
@Tag(name = "Контроллер для работы с хранилищем")
@RestController
@RequestMapping("/api/v1")
public class StorageController {

    private final ResourceService resourceService;

    @Autowired
    public StorageController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }


    @Operation(summary = "Загрузка аватара пользователя в хранилище сервиса")
    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping(value = "/storage", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<DataResponse<AvatarUploadData>> saveUserAvatar(@ModelAttribute ChangeAvatarRequest changeAvatarRequest,
                                                                         Principal principal) throws IOException {
        DataResponse<AvatarUploadData> response = new DataResponse<>();
        response.setTimestamp(LocalDateTime.now());
        AvatarUploadData data = resourceService.saveUserAvatar(changeAvatarRequest.getFile(), principal);
        response.setData(data);
        log.info("Пользователь {} сохранил свой аватар в хранилище", principal.getName());
        return ResponseEntity.ok(response);
    }

}
