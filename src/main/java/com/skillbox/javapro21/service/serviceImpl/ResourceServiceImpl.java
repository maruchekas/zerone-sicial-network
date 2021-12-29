package com.skillbox.javapro21.service.serviceImpl;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.skillbox.javapro21.api.response.Content;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.MessageOkContent;
import com.skillbox.javapro21.api.response.account.AvatarUploadData;
import com.skillbox.javapro21.config.Constants;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.repository.PersonRepository;
import com.skillbox.javapro21.service.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component
public class ResourceServiceImpl extends AbstractMethodClass implements ResourceService {

    private final PersonRepository personRepository;
    private final Cloudinary cloudinary;

    @Autowired
    public ResourceServiceImpl(PersonRepository personRepository, Cloudinary cloudinary) {
        super(personRepository);
        this.personRepository = personRepository;
        this.cloudinary = cloudinary;
    }


    @Override
    public DataResponse<Content> saveFileInStorage(String type, MultipartFile image, Principal principal) throws IOException {
        Person person = ((Person)(((UsernamePasswordAuthenticationToken) principal).getPrincipal()));
        DataResponse<Content> response = new DataResponse<>()
                                            .setTimestamp(LocalDateTime.now());

        if (image == null) {
            log.info("Не принимаем никакой файл в хранилище");
            return response.setData(new MessageOkContent().setMessage("Ничего не сохраняем"));
        }

        switch (type) {
            case "IMAGE" -> {
                AvatarUploadData data = saveUserAvatar(image, person);
                response.setData(data);
                log.info("Пользователь {} сохранил свой аватар в хранилище", person.getEmail());
                return response;
            }
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }



    }

    private AvatarUploadData saveUserAvatar(MultipartFile image, Person person) throws IOException {
        Map params = ObjectUtils.asMap(
                "public_id", Constants.CLOUDINARY_AVATARS_FOLDER + person.getEmail(),
                "transformation", new Transformation<>().width(1024).height(1024)
        );
        Map uploadResult = cloudinary.uploader().upload(image.getBytes(), params);
        String url = uploadResult.get("url").toString();
        AvatarUploadData data = new AvatarUploadData()
                .setOwnerId(person.getId())
                .setFileName(image.getOriginalFilename())
                .setRelativeFilePath(url)
                .setRawFileURL(url)
                .setFileFormat(image.getContentType())
                .setBytes(image.getSize())
                .setCreatedAt(new Date().getTime());
        person.setPhoto(url);
        personRepository.save(person);
        return data;
    }
}
