package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.aop.LastActivity;
import com.skillbox.javapro21.api.request.post.PostRequest;
import com.skillbox.javapro21.api.request.profile.EditProfileRequest;
import com.skillbox.javapro21.api.response.Content;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.MessageOkContent;
import com.skillbox.javapro21.api.response.account.AuthData;
import com.skillbox.javapro21.api.response.post.PostData;
import com.skillbox.javapro21.exception.*;
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

import java.nio.file.attribute.UserPrincipalNotFoundException;
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
    @LastActivity
    public ResponseEntity<DataResponse<AuthData>> getPerson(Principal principal) {
        return new ResponseEntity<>(profileService.getPerson(principal), HttpStatus.OK);
    }

    @Operation(summary = "Редактирование текущего пользователя", security = @SecurityRequirement(name = "jwt"))
    @PutMapping("/me")
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<DataResponse<AuthData>> editPerson(@RequestBody EditProfileRequest editProfileRequest,
                                                             Principal principal) throws UserPrincipalNotFoundException {
        return new ResponseEntity<>(profileService.editPerson(principal, editProfileRequest), HttpStatus.OK);
    }

    @Operation(summary = "Удаление пользователем его аккаунта", security = @SecurityRequirement(name = "jwt"))
    @DeleteMapping("/me")
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<DataResponse<MessageOkContent>> deletePerson(Principal principal) {
        return new ResponseEntity<>(profileService.deletePerson(principal), HttpStatus.OK);
    }

    @Operation(summary = "Получить пользователя по id", security = @SecurityRequirement(name = "jwt"))
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<DataResponse<AuthData>> getPersonById(@PathVariable Long id) throws PersonNotFoundException {
        return new ResponseEntity<>(profileService.getPersonById(id), HttpStatus.OK);
    }

    @Operation(summary = "Получить публикации пользователя на стене", security = @SecurityRequirement(name = "jwt"))
    @GetMapping("/{id}/wall")
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<ListDataResponse<PostData>> getPersonWallById(@PathVariable Long id,
                                                                        @RequestParam(name = "offset", defaultValue = "0") int offset,
                                                                        @RequestParam(name = "item_per_page", defaultValue = "20") int itemPerPage,
                                                                        Principal principal) throws InterlockedFriendshipStatusException, PersonNotFoundException {
        return new ResponseEntity<>(profileService.getPersonWallById(id, offset, itemPerPage, principal), HttpStatus.OK);
    }

    @Operation(summary = "Сделать убликацию пользователем на стене", security = @SecurityRequirement(name = "jwt"))
    @PostMapping("/{id}/wall")
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<DataResponse<PostData>> postPostOnPersonWallById(@PathVariable Long id,
                                                                           @RequestParam(name = "publish_date", defaultValue = "-1") Long publishDate,
                                                                           @RequestBody PostRequest postRequest,
                                                                           Principal principal) throws InterlockedFriendshipStatusException, PersonNotFoundException, PostNotFoundException {
        return new ResponseEntity<>(profileService.postPostOnPersonWallById(id, publishDate, postRequest, principal), HttpStatus.OK);
    }

    @Operation(summary = "Заблокировать пользователя по id", security = @SecurityRequirement(name = "jwt"))
    @PutMapping("block/{id}")
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<DataResponse<MessageOkContent>> blockPersonById(@PathVariable Long id,Principal principal) throws InterlockedFriendshipStatusException, BlockPersonHimselfException, PersonNotFoundException, FriendshipNotFoundException {
        return new ResponseEntity<>(profileService.blockPersonById(id, principal), HttpStatus.OK);
    }

    @Operation(summary = "Разблокировать пользователя по id", security = @SecurityRequirement(name = "jwt"))
    @DeleteMapping("block/{id}")
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<DataResponse<MessageOkContent>> unblockPersonById(@PathVariable Long id,Principal principal) throws InterlockedFriendshipStatusException, BlockPersonHimselfException, PersonNotFoundException, NonBlockedFriendshipException, FriendshipNotFoundException {
        return new ResponseEntity<>(profileService.unblockPersonById(id, principal), HttpStatus.OK);
    }

    @Operation(summary = "Поиск по пользователю", security = @SecurityRequirement(name = "jwt"))
    @LastActivity
    @PreAuthorize("hasAuthority('user:write')")
    @GetMapping("/search")
    public ResponseEntity<ListDataResponse<Content>> searchByPerson(@RequestParam(name = "first_name", defaultValue = "") String firstName,
                                                                    @RequestParam(name = "last_name", defaultValue = "") String lastName,
                                                                    @RequestParam(name = "age_from", defaultValue = "0") Integer ageFrom,
                                                                    @RequestParam(name = "age_to", defaultValue = "150") Integer ageTo,
                                                                    @RequestParam(name = "country", defaultValue = "") String country,
                                                                    @RequestParam(name = "city", defaultValue = "") String city,
                                                                    @RequestParam(name = "offset", defaultValue = "0") Integer offset,
                                                                    @RequestParam(name = "limit", defaultValue = "20") Integer limit,
                                                                    Principal principal) {
        return new ResponseEntity<>(profileService.searchByPerson(firstName, lastName, ageFrom, ageTo, country, city, offset, limit, principal), HttpStatus.OK);
    }
}
