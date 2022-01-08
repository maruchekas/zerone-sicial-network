package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.dialogs.DialogsData;
import com.skillbox.javapro21.service.DialogsService;
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
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ListDataResponse<DialogsData>> getDialogs(@RequestParam(name = "offset", defaultValue = "0") int offset,
                                                                    @RequestParam(name = "item_per_page", defaultValue = "20") int itemPerPage,
                                                                    @RequestParam String query,
                                                                    Principal principal) {
        return new ResponseEntity<>(dialogsService.getDialogs(query, offset, itemPerPage, principal), HttpStatus.OK);
    }
}
