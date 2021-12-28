package com.skillbox.javapro21.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.skillbox.javapro21.api.response.account.AvatarUploadData;
import com.skillbox.javapro21.config.Constants;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.repository.PersonRepository;
import com.skillbox.javapro21.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Date;
import java.util.Map;

@Component
public class ResourceServiceImpl implements ResourceService {

    private final PersonRepository personRepository;
    private final Cloudinary cloudinary;

    @Autowired
    public ResourceServiceImpl(PersonRepository personRepository, Cloudinary cloudinary) {
        this.personRepository = personRepository;
        this.cloudinary = cloudinary;
    }


    @Override
    public AvatarUploadData saveUserAvatar(MultipartFile image, Principal principal) throws IOException {
        Person person = ((Person)(((UsernamePasswordAuthenticationToken) principal).getPrincipal()));
        Map params = ObjectUtils.asMap(
                "public_id", Constants.CLOUDINARY_AVATARS_FOLDER + person.getEmail(),
                "transformation", new Transformation<>().width(1024).height(1024)
        );
        Map uploadResult = cloudinary.uploader().upload(image.getBytes(), params);
        String url = uploadResult.get("url").toString();
        AvatarUploadData data = new AvatarUploadData()
                                    .setOwnerId(person.getId())
                                    .setFileName(image.getName())
                                    .setRelativeFilePath(url)
                                    .setRawFileURL(image.getOriginalFilename())
                                    .setFileFormat(image.getContentType())
                                    .setBytes(image.getSize())
                                    .setCreatedAt(new Date().getTime());
        person.setPhoto(url);
        personRepository.save(person);
        return data;
    }
}
