package com.skillbox.javapro21.service;

import com.skillbox.javapro21.api.response.FileUploadResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@Service
public interface ResourceService {

    FileUploadResponse saveNewUserAvatar(MultipartFile image, Principal principal) throws IOException;

}
