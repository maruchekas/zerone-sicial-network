package com.skillbox.javapro21.service;

import com.skillbox.javapro21.api.response.Content;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.domain.Person;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@Service
public interface ResourceService {

    DataResponse<Content> saveFileInStorage(String type, MultipartFile image, Principal principal) throws IOException;

    String saveDefaultAvatarToPerson(Person person) throws IOException;
}
