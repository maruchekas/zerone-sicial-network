package com.skillbox.javapro21.service;

import com.skillbox.javapro21.api.response.account.AvatarUploadData;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@Service
public interface ResourceService {

    AvatarUploadData saveUserAvatar(MultipartFile image, Principal principal) throws IOException;

}
