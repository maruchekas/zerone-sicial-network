package com.skillbox.javapro21.service.impl;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Map;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {
    private final PersonRepository personRepository;
    private final Cloudinary cloudinary;

    public String setDefaultAvatarToUser(String userEmail) throws IOException {
        String format = "png";
        int setNum = new Random().nextInt(4) + 1;
        String urlCreatedAvatar =  Constants.BASE_ROBOTIC_AVA_URL
                + userEmail + Constants.AVATAR_CONFIG + setNum;

        Map params = getMap(userEmail, 360);
        BufferedImage image = ImageIO.read(new URL(urlCreatedAvatar));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, format, baos);
        byte[] bytes = baos.toByteArray();

        Map uploadResult = cloudinary.uploader().upload(bytes, params);

        return uploadResult.get("url").toString();
    }

    private Map getMap(String userEmail, int transformationParams) {
        return ObjectUtils.asMap(
                "public_id", Constants.CLOUDINARY_AVATARS_FOLDER + userEmail,
                "transformation", new Transformation<>().width(transformationParams).height(transformationParams)
        );
    }

    @Override
    public DataResponse<Content> saveFileInStorage(String type, MultipartFile image, Principal principal) throws IOException {
        Person person = ((Person)(((UsernamePasswordAuthenticationToken) principal).getPrincipal()));
        DataResponse<Content> response = new DataResponse<>()
                                            .setTimestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());

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
        Map params = getMap(person.getEmail(), 1024);
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
