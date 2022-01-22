package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.platform.LanguageData;
import com.skillbox.javapro21.service.LanguageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Контроллер для работы с платформой")
@RequestMapping("/api/v1/platform")
public class PlatformController {

    private final LanguageService languageService;

    @GetMapping("/languages")
    ResponseEntity<ListDataResponse<LanguageData>> getLanguage(
            @RequestParam(name = "language", defaultValue = "Русский") String language,
            @RequestParam(name = "offset", defaultValue = "0") int offset,
            @RequestParam(name = "item_per_page", defaultValue = "20") int itemPerPage) {


        return new ResponseEntity<>(languageService.getLanguage(language, offset, itemPerPage), HttpStatus.OK);
    }
}
