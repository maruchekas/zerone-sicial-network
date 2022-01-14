package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.aop.LastActivity;
import com.skillbox.javapro21.api.request.dialogs.DialogRequestForCreate;
import com.skillbox.javapro21.api.request.dialogs.LincRequest;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.dialogs.*;
import com.skillbox.javapro21.exception.PersonNotFoundException;
import com.skillbox.javapro21.service.DialogsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@RestController
@Tag(name = "Контроллер для работы с диалогами")
@RequestMapping("/api/v1/dialogs")
@RequiredArgsConstructor
public class DialogsController {
    private final DialogsService dialogsService;

    @GetMapping("")
    @Operation(summary = "Получение диалогов")
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<ListDataResponse<DialogsData>> getDialogs(@RequestParam(name = "offset", defaultValue = "0") int offset,
                                                                    @RequestParam(name = "item_per_page", defaultValue = "20") int itemPerPage,
                                                                    @RequestParam(name = "query", defaultValue = "") String query,
                                                                    Principal principal) {
        return new ResponseEntity<>(dialogsService.getDialogs(query, offset, itemPerPage, principal), HttpStatus.OK);
    }

    @PostMapping("")
    @Operation(summary = "Добавление диалога")
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<DataResponse<DialogsData>> createDialog(@RequestBody DialogRequestForCreate dialogRequestForCreate,
                                                                  Principal principal) throws PersonNotFoundException {
        return new ResponseEntity<>(dialogsService.createDialog(dialogRequestForCreate, principal), HttpStatus.OK);
    }

    @GetMapping("/unreaded")
    @Operation(summary = "Получение кол-ва непрочтенных сообщений")
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<DataResponse<CountContent>> getUnreadedDialogs(Principal principal) {
        return new ResponseEntity<>(dialogsService.getUnreadedDialogs(principal), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление диалога")
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<DataResponse<DialogsData>> deleteDialog(@PathVariable int id) {
        return new ResponseEntity<>(dialogsService.deleteDialog(id), HttpStatus.OK);
    }

    @PutMapping("/{id}/users")
    @Operation(summary = "Добавление пользователя в существующий диалог")
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<DataResponse<DialogPersonIdContent>> putPersonsInDialog(@PathVariable int id,
                                                                                  @RequestBody DialogRequestForCreate listPersons,
                                                                                  Principal principal) {
        return new ResponseEntity<>(dialogsService.putPersonsInDialog(id, listPersons, principal), HttpStatus.OK);
    }

    @DeleteMapping("/{id}/users")
    @Operation(summary = "Удаление пользователей из диалога")
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<DataResponse<DialogPersonIdContent>> deletePersonsInDialog(@PathVariable int id,
                                                                                     @RequestBody DialogRequestForCreate listPersons,
                                                                                     Principal principal) {
        return new ResponseEntity<>(dialogsService.deletePersonsInDialog(id, listPersons, principal), HttpStatus.OK);
    }

    @GetMapping("/{id}/users/invite")
    @Operation(summary = "Получить ссылку-приглашение в диалог")
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<DataResponse<LinkContent>> inviteLink(@PathVariable int id,
                                                                Principal principal) {
        return new ResponseEntity<>(dialogsService.inviteLink(id, principal), HttpStatus.OK);
    }

    @PutMapping("/{id}/users/join")
    @Operation(summary = "Присоедениться к диалогу по ссылке-приглашению")
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<DataResponse<DialogPersonIdContent>> joinInLink(@PathVariable int id,
                                                                          @RequestBody LincRequest lincRequest,
                                                                          Principal principal) {
        return new ResponseEntity<>(dialogsService.joinInLink(id, lincRequest, principal), HttpStatus.OK);
    }

    @GetMapping("/{id}/messages")
    @Operation(summary = "Получение списка сообщений в диалога")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ListDataResponse<MessageData>> getMessagesById(@PathVariable int id,
                                                                     @RequestParam(name = "query", defaultValue = "") String query,
                                                                     @RequestParam(name = "offset", defaultValue = "0") int offset,
                                                                     @RequestParam(name = "item_per_page", defaultValue = "20") int itemPerPage,
                                                                     @RequestParam(name = "fromMessageId", defaultValue = "-1") int fromMessageId,
                                                                     Principal principal) {
        return new ResponseEntity<>(dialogsService.getMessagesById(id, query, offset, itemPerPage, fromMessageId, principal), HttpStatus.OK);
    }
}