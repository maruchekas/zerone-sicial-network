package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.aop.LastActivity;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.tag.TagData;
import com.skillbox.javapro21.exception.BadArgumentException;
import com.skillbox.javapro21.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Контроллер для работы с тэгами")
@RequiredArgsConstructor
@RequestMapping("/api/v1/tags")
public class TagController {

    private final TagService tagService;

    @GetMapping("")
    @Operation(summary = "Получение списка тегов по имени или его части", security = @SecurityRequirement(name = "jwt"))
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    ResponseEntity<ListDataResponse<TagData>> getTags(
            @RequestParam(name = "tag", defaultValue = "") String tag,
            @RequestParam(name = "offset", defaultValue = "0") int offset,
            @RequestParam(name = "item_per_page", defaultValue = "20") int itemPerPage) {
        return new ResponseEntity<>(tagService.getTags(tag, offset, itemPerPage), HttpStatus.OK);
    }

    @PostMapping("")
    @Operation(summary = "Добавление тэга", security = @SecurityRequirement(name = "jwt"))
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    ResponseEntity<DataResponse<TagData>> addTag(
            @RequestBody String tag ){
        return new ResponseEntity<>(tagService.addTag(tag), HttpStatus.OK);
    }

    @DeleteMapping("")
    @Operation(summary = "Удаление тега по id", security = @SecurityRequirement(name = "jwt"))
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    ResponseEntity<DataResponse<TagData>> deleteTag(
            @RequestParam(name = "id", defaultValue = "") long id) throws BadArgumentException {
        return new ResponseEntity<>(tagService.deleteTagById(id), HttpStatus.OK);
    }
}
