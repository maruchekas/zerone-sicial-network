package com.skillbox.javapro21.api.request.profile;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ChangeAvatarRequest {

    private MultipartFile file;
}
