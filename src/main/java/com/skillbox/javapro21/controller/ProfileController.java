package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Slf4j
@RestController
@Tag(name = "Контроллер для работы с профилем пользователя")
@RequestMapping("/api/v1/users")
public class ProfileController {

    private final ProfileService profileService;

    @Autowired
    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Operation(summary = "Удаление пользователем его аккаунта", security = @SecurityRequirement(name = "jwt"))
    @DeleteMapping("/me")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<DataResponse> deletePerson(Principal principal) {
        return new ResponseEntity<>(profileService.deletePerson(principal), HttpStatus.OK);
    }
}
