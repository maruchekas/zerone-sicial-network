package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.aop.LastActivity;
import com.skillbox.javapro21.api.request.dialogs.DialogRequestForCreate;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.MessageOkContent;
import com.skillbox.javapro21.api.response.account.AuthData;
import com.skillbox.javapro21.api.response.friends.StatusContent;
import com.skillbox.javapro21.service.FriendsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@RestController
@Tag(name = "Контроллер для работы с друзьями")
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FriendsController {
    private final FriendsService friendsService;

    @Operation(summary = "Получение списка друзей")
    @GetMapping("/friends")
    @LastActivity
    public ResponseEntity<ListDataResponse<AuthData>> getFriends(@RequestParam(name = "name", defaultValue = "") String name,
                                                                 @RequestParam(name = "offset", defaultValue = "0") int offset,
                                                                 @RequestParam(name = "item_per_page", defaultValue = "20") int itemPerPage,
                                                                 Principal principal) {
        return new ResponseEntity<>(friendsService.getFriends(name, offset, itemPerPage, principal), HttpStatus.OK);
    }

    @Operation(summary = "Удаление пользователя из друзей")
    @DeleteMapping("/friends/{id}")
    @LastActivity
    public ResponseEntity<DataResponse<MessageOkContent>> deleteFriend(@PathVariable Long id,
                                                                       Principal principal) {
        return new ResponseEntity<>(friendsService.deleteFriend(id, principal), HttpStatus.OK);
    }

    @Operation(summary = "Принять/Добавить пользователя в друзья")
    @PostMapping("/friends/{id}")
    @LastActivity
    public ResponseEntity<DataResponse<MessageOkContent>> editFriend(@PathVariable Long id,
                                                                     Principal principal) {
        return new ResponseEntity<>(friendsService.editFriend(id, principal), HttpStatus.OK);
    }

    @Operation(summary = "Получить список входящик заявок на добавление в друзья")
    @GetMapping("/friends/request")
    @LastActivity
    public ResponseEntity<ListDataResponse<AuthData>> requestFriends(@RequestParam(name = "name", defaultValue = "") String name,
                                                                     @RequestParam(name = "offset", defaultValue = "0") int offset,
                                                                     @RequestParam(name = "item_per_page", defaultValue = "20") int itemPerPage,
                                                                     Principal principal) {
        return new ResponseEntity<>(friendsService.requestFriends(name, offset, itemPerPage, principal), HttpStatus.OK);
    }

    @Operation(summary = "Получить список рекомендаций")
    @GetMapping("/friends/recommendations")
    @LastActivity
    public ResponseEntity<ListDataResponse<AuthData>> recommendationsFriends(@RequestParam(name = "offset", defaultValue = "0") int offset,
                                                                             @RequestParam(name = "item_per_page", defaultValue = "20") int itemPerPage,
                                                                             Principal principal) {
        return new ResponseEntity<>(friendsService.recommendationsFriends(offset, itemPerPage, principal), HttpStatus.OK);
    }

    @Operation(summary = "Получить информацию является ли пользователь другом указанных пользователей")
    @PostMapping("/is/friends")
    @LastActivity
    public ResponseEntity<DataResponse<StatusContent>> isFriend(@RequestBody DialogRequestForCreate users,
                                                                Principal principal) {
        return new ResponseEntity<>(friendsService.isFriend(users, principal), HttpStatus.OK);
    }

    @Operation(summary = "Получить список заблокированных пользователей")
    @GetMapping("/friends/blocked")
    @LastActivity
    public ResponseEntity<ListDataResponse<AuthData>> blockedPersons(@RequestParam(name = "name", defaultValue = "") String name,
                                                                     @RequestParam(name = "offset", defaultValue = "0") int offset,
                                                                     @RequestParam(name = "item_per_page", defaultValue = "20") int itemPerPage,
                                                                     Principal principal) {
        return new ResponseEntity<>(friendsService.blockedFriends(name, offset, itemPerPage, principal), HttpStatus.OK);
    }
}
