package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.api.request.ChangeAvatarRequest;
import com.skillbox.javapro21.api.response.CommonOkResponse;
import com.skillbox.javapro21.service.ResourceService;
import com.skillbox.javapro21.service.serviceImpl.ResourceServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.Principal;
import java.util.Date;

@RestController
@RequestMapping("/api/v1/storage")
public class StorageController {

    private final ResourceService resourceService;

    @Autowired
    public StorageController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }


    @PostMapping(value = "/", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<CommonOkResponse> saveUserAvatar(@ModelAttribute ChangeAvatarRequest changeAvatarRequest,
                                                          Principal principal) throws IOException {
        CommonOkResponse response = new CommonOkResponse();
        response.setTimestamp(new Date().getTime());
        response.setData(resourceService.saveNewUserAvatar(changeAvatarRequest.getPhoto(), principal));

        return ResponseEntity.ok(response);
    }

}
