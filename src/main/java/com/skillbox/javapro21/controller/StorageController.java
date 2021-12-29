package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.api.response.Content;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.service.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<DataResponse<Content>> saveUserAvatar(@RequestParam("type") String type,
                                                                @RequestParam(value = "file", required = false) MultipartFile file,
                                                                Principal principal) throws IOException {
        DataResponse<Content> response = resourceService.saveFileInStorage(type, file, principal);
        return ResponseEntity.ok(response);
    }
}
