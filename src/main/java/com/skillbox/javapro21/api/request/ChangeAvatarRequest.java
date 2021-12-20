package com.skillbox.javapro21.api.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ChangeAvatarRequest {

    private MultipartFile photo;

}
