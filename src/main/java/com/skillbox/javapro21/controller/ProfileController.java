package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.api.request.profile.PostRequest;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.exception.PersonNotFoundException;
import com.skillbox.javapro21.service.ProfileService;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.MessageOkContent;
import com.skillbox.javapro21.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{id}")
    public ResponseEntity<DataResponse> getUser(@PathVariable long id) throws PersonNotFoundException {
        return new ResponseEntity<>(profileService.getPerson(id), HttpStatus.OK);
    }

    @PostMapping("{id}/wall")
    public ResponseEntity putPostToWall(@PathVariable long id,
                                        @RequestParam(name = "publish_date") long publishDate,
                                        @RequestBody PostRequest postRequest) throws PersonNotFoundException {
        return new ResponseEntity<>(profileService.post(id, publishDate, postRequest), HttpStatus.OK);
    }

    @GetMapping("/{id}/wall")
    public ResponseEntity getWall(@PathVariable long id,
                                    @RequestParam int offset,
                                    @RequestParam int itemPerPage) {
        return new ResponseEntity<>(profileService.getWall(id, offset, itemPerPage), HttpStatus.OK);
    }

    @Operation(summary = "Удаление пользователем его аккаунта", security = @SecurityRequirement(name = "jwt"))
    @DeleteMapping("/me")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<DataResponse<MessageOkContent>> deletePerson(Principal principal) {
        return new ResponseEntity<>(profileService.deletePerson(principal), HttpStatus.OK);
    }
}
