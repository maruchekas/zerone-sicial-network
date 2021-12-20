package com.skillbox.javapro21.service.serviceImpl;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.skillbox.javapro21.api.response.FileUploadResponse;
import com.skillbox.javapro21.config.Constants;
import com.skillbox.javapro21.service.AccountService;
import com.skillbox.javapro21.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Date;
import java.util.Map;

@Component
public class ResourceServiceImpl implements ResourceService {

    private final Cloudinary cloudinary;
    private final AccountService accountService;

    @Autowired
    public ResourceServiceImpl(Cloudinary cloudinary, AccountService accountService) {
        this.cloudinary = cloudinary;
        this.accountService = accountService;
    }


    @Override
    public FileUploadResponse saveNewUserAvatar(MultipartFile image, Principal principal) throws IOException {
        Map params = ObjectUtils.asMap(
                "public_id", Constants.CLOUDINARY_AVATARS_FOLDER + principal.getName(),
                "transformation", new Transformation<>().width(36).height(36)
        );
        Map uploadResult = cloudinary.uploader().upload(image.getBytes(), params);
        String url = uploadResult.get("url").toString();

        return new FileUploadResponse(
                accountService.findPersonByEmail(principal.getName()).getId(),
                image.getName(),
                url,
                image.getOriginalFilename(),
                image.getContentType(),
                image.getSize(),
                new Date().getTime()
                );
    }

}
