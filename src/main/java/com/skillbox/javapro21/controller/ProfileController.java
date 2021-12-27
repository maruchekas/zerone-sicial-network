package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.api.request.profile.EditProfileRequest;
import com.skillbox.javapro21.api.request.profile.PostRequest;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.MessageOkContent;
import com.skillbox.javapro21.api.response.account.AuthData;
import com.skillbox.javapro21.api.response.profile.PostContent;
import com.skillbox.javapro21.exception.PersonNotFoundException;
import com.skillbox.javapro21.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "Получить текущего пользователя", security = @SecurityRequirement(name = "jwt"))
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<DataResponse<AuthData>> getPerson(Principal principal) throws PersonNotFoundException {
        return new ResponseEntity<>(profileService.getPerson(principal), HttpStatus.OK);
    }

    @Operation(summary = "Редактирование текущего пользователя", security = @SecurityRequirement(name = "jwt"))
    @PutMapping("/me")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<DataResponse<AuthData>> editPerson(Principal principal, @RequestBody EditProfileRequest editProfileRequest) {
        return new ResponseEntity<>(profileService.editPerson(principal, editProfileRequest), HttpStatus.OK);
    }

    @Operation(summary = "Удаление пользователем его аккаунта", security = @SecurityRequirement(name = "jwt"))
    @DeleteMapping("/me")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<DataResponse<MessageOkContent>> deletePerson(Principal principal) {
        return new ResponseEntity<>(profileService.deletePerson(principal), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<DataResponse> getPersonById(@PathVariable long id) throws PersonNotFoundException {
        return new ResponseEntity<>(profileService.getPersonById(id), HttpStatus.OK);
    }

    @PostMapping("{id}/wall")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<DataResponse<PostContent>> putPost(@PathVariable long id,
                                                               @RequestParam(name = "publish_date") long publishDate,
                                                               @RequestBody PostRequest postRequest) throws PersonNotFoundException {
        return new ResponseEntity<>(profileService.post(id, publishDate, postRequest), HttpStatus.OK);
    }

    @GetMapping("/{id}/wall")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ListDataResponse> getWall(@PathVariable long id,
                                                                 @RequestParam int offset,
                                                                 @RequestParam int itemPerPage) throws PersonNotFoundException {
        return new ResponseEntity<>(profileService.getWall(id, offset, itemPerPage), HttpStatus.OK);
    }

}
