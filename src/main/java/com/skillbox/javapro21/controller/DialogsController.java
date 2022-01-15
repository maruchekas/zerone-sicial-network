package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.aop.LastActivity;
import com.skillbox.javapro21.api.request.dialogs.DialogRequestForCreate;
import com.skillbox.javapro21.api.request.dialogs.LincRequest;
import com.skillbox.javapro21.api.request.dialogs.MessageTextRequest;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.MessageOkContent;
import com.skillbox.javapro21.api.response.dialogs.*;
import com.skillbox.javapro21.exception.MessageNotFoundException;
import com.skillbox.javapro21.exception.PersonNotFoundException;
import com.skillbox.javapro21.service.DialogsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
    @Operation(summary = "Получение диалогов", security = @SecurityRequirement(name = "jwt"))
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<ListDataResponse<DialogContent>> getDialogs(@RequestParam(name = "offset", defaultValue = "0") int offset,
                                                                      @RequestParam(name = "item_per_page", defaultValue = "20") int itemPerPage,
                                                                      @RequestParam(name = "query", defaultValue = "") String query,
                                                                      Principal principal) {
        return new ResponseEntity<>(dialogsService.getDialogs(query, offset, itemPerPage, principal), HttpStatus.OK);
    }

    @PostMapping("")
    @Operation(summary = "Добавление диалога", security = @SecurityRequirement(name = "jwt"))
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<DataResponse<DialogContent>> createDialog(@RequestBody DialogRequestForCreate dialogRequestForCreate,
                                                                    Principal principal) throws PersonNotFoundException {
        return new ResponseEntity<>(dialogsService.createDialog(dialogRequestForCreate, principal), HttpStatus.OK);
    }

    @GetMapping("/unreaded")
    @Operation(summary = "Получение кол-ва непрочтенных сообщений", security = @SecurityRequirement(name = "jwt"))
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<DataResponse<CountContent>> getUnreadedDialogs(Principal principal) {
        return new ResponseEntity<>(dialogsService.getUnreadedDialogs(principal), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление диалога", security = @SecurityRequirement(name = "jwt"))
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<DataResponse<DialogContent>> deleteDialog(@PathVariable int id) {
        return new ResponseEntity<>(dialogsService.deleteDialog(id), HttpStatus.OK);
    }

    @PutMapping("/{id}/users")
    @Operation(summary = "Добавление пользователя в существующий диалог", security = @SecurityRequirement(name = "jwt"))
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<DataResponse<DialogPersonIdContent>> putPersonsInDialog(@PathVariable int id,
                                                                                  @RequestBody DialogRequestForCreate listPersons,
                                                                                  Principal principal) {
        return new ResponseEntity<>(dialogsService.putPersonsInDialog(id, listPersons, principal), HttpStatus.OK);
    }

    @DeleteMapping("/{id}/users")
    @Operation(summary = "Удаление пользователей из диалога", security = @SecurityRequirement(name = "jwt"))
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<DataResponse<DialogPersonIdContent>> deletePersonsInDialog(@PathVariable int id,
                                                                                     @RequestBody DialogRequestForCreate listPersons,
                                                                                     Principal principal) {
        return new ResponseEntity<>(dialogsService.deletePersonsInDialog(id, listPersons, principal), HttpStatus.OK);
    }

    @GetMapping("/{id}/users/invite")
    @Operation(summary = "Получить ссылку-приглашение в диалог", security = @SecurityRequirement(name = "jwt"))
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<DataResponse<LinkContent>> inviteLink(@PathVariable int id,
                                                                Principal principal) {
        return new ResponseEntity<>(dialogsService.inviteLink(id, principal), HttpStatus.OK);
    }

    @PutMapping("/{id}/users/join")
    @Operation(summary = "Присоедениться к диалогу по ссылке-приглашению", security = @SecurityRequirement(name = "jwt"))
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<DataResponse<DialogPersonIdContent>> joinInLink(@PathVariable int id,
                                                                          @RequestBody LincRequest lincRequest,
                                                                          Principal principal) {
        return new ResponseEntity<>(dialogsService.joinInLink(id, lincRequest, principal), HttpStatus.OK);
    }

    @GetMapping("/{id}/messages")
    @Operation(summary = "Получение списка сообщений в диалога", security = @SecurityRequirement(name = "jwt"))
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ListDataResponse<MessageContent>> getMessagesById(@PathVariable int id,
                                                                            @RequestParam(name = "query", defaultValue = "") String query,
                                                                            @RequestParam(name = "offset", defaultValue = "0") int offset,
                                                                            @RequestParam(name = "item_per_page", defaultValue = "20") int itemPerPage,
                                                                            @RequestParam(name = "fromMessageId", defaultValue = "-1") int fromMessageId,
                                                                            Principal principal) {
        return new ResponseEntity<>(dialogsService.getMessagesById(id, query, offset, itemPerPage, fromMessageId, principal), HttpStatus.OK);
    }

    @PostMapping("/{id}/messages")
    @Operation(summary = "Отправка сообщений", security = @SecurityRequirement(name = "jwt"))
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<DataResponse<MessageContent>> postMessagesById(@PathVariable int id,
                                                                         @RequestBody MessageTextRequest messageText,
                                                                         Principal principal) {
        return new ResponseEntity<>(dialogsService.postMessagesById(id, messageText, principal), HttpStatus.OK);
    }

    @DeleteMapping("/{dialog_id}/messages/{message_id}")
    @Operation(summary = "Удаление сообщения", security = @SecurityRequirement(name = "jwt"))
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<DataResponse<MessageIdContent>> deleteMessageById(@PathVariable(name = "dialog_id") int dialogId,
                                                                            @PathVariable(name = "message_id") Long messageId,
                                                                            Principal principal) {
        return new ResponseEntity<>(dialogsService.deleteMessageById(dialogId, messageId, principal), HttpStatus.OK);
    }

    @PutMapping("/{dialog_id}/messages/{message_id}")
    @Operation(summary = "Редактирование сообщения", security = @SecurityRequirement(name = "jwt"))
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<DataResponse<MessageContent>> putMessageById(@PathVariable(name = "dialog_id") int dialogId,
                                                                       @PathVariable(name = "message_id") Long messageId,
                                                                       @RequestBody MessageTextRequest messageText,
                                                                       Principal principal) {
        return new ResponseEntity<>(dialogsService.putMessageById(dialogId, messageId, messageText, principal), HttpStatus.OK);
    }

    @PutMapping("/{dialog_id}/messages/{message_id}/recover")
    @Operation(summary = "Восстановление сообщения", security = @SecurityRequirement(name = "jwt"))
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<DataResponse<MessageContent>> putRecoverMessageById(@PathVariable(name = "dialog_id") int dialogId,
                                                                              @PathVariable(name = "message_id") Long messageId,
                                                                              Principal principal) throws MessageNotFoundException {
        return new ResponseEntity<>(dialogsService.putRecoverMessageById(dialogId, messageId, principal), HttpStatus.OK);
    }

    @PutMapping("/{dialog_id}/messages/{message_id}/read")
    @Operation(summary = "Отметить сообщение прочтенным", security = @SecurityRequirement(name = "jwt"))
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<DataResponse<MessageOkContent>> readeMessage(@PathVariable(name = "dialog_id") int dialogId,
                                                                       @PathVariable(name = "message_id") Long messageId,
                                                                       Principal principal) throws MessageNotFoundException {
        return new ResponseEntity<>(dialogsService.readeMessage(dialogId, messageId, principal), HttpStatus.OK);
    }

    @GetMapping("/{id}/activity/{user_id}")
    @Operation(summary = "Получить последнюю активность и текущий статус для пользователя с которым ведется диалог", security = @SecurityRequirement(name = "jwt"))
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<DataResponse<LastActivityContent>> activityPersonInDialog(@PathVariable int id,
                                                                                    @PathVariable(name = "user_id") Long userId,
                                                                                    Principal principal) {
        return new ResponseEntity<>(dialogsService.activityPersonInDialog(id, userId, principal), HttpStatus.OK);
    }

    @PostMapping("/{id}/activity/{user_id}")
    @Operation(summary = "Изменить статус набора текста пользователем в диалоге.", security = @SecurityRequirement(name = "jwt"))
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<DataResponse<MessageOkContent>> postActivityPersonInDialog(@PathVariable int id,
                                                                                     @PathVariable(name = "user_id") Long userId,
                                                                                     Principal principal) throws PersonNotFoundException {
        return new ResponseEntity<>(dialogsService.postActivityPersonInDialog(id, userId, principal), HttpStatus.OK);
    }
}