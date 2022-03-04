package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.StringListDataResponse;
import com.skillbox.javapro21.api.response.platform.LanguageData;
import com.skillbox.javapro21.api.response.platform.WeatherResponse;
import com.skillbox.javapro21.exception.UnauthorizedUserException;
import com.skillbox.javapro21.service.LanguageService;
import com.skillbox.javapro21.service.LocationService;
import com.skillbox.javapro21.service.impl.WeatherMapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@Tag(name = "Контроллер для работы с платформой")
@RequestMapping("/api/v1/platform")
public class PlatformController {
    private final LanguageService languageService;
    private final LocationService locationService;
    private final WeatherMapService weatherMapService;

    @Operation(summary = "Получение показаний погоды для текущего пользователя")
    @PreAuthorize("hasAuthority('user:write')")
    @GetMapping("/weather")
    ResponseEntity<WeatherResponse> getWeather(Principal principal) throws UnauthorizedUserException, IOException, ParseException {
        return new ResponseEntity<>(weatherMapService.showWeather(principal), HttpStatus.OK);
    }

    @Operation(summary = "Получение списка языков")
    @GetMapping("/languages")
    ResponseEntity<ListDataResponse<LanguageData>> getLanguage(
            @RequestParam(name = "language", defaultValue = "Русский") String language,
            @RequestParam(name = "offset", defaultValue = "0") int offset,
            @RequestParam(name = "item_per_page", defaultValue = "20") int itemPerPage) {
        return new ResponseEntity<>(languageService.getLanguage(language, offset, itemPerPage), HttpStatus.OK);
    }

    @Operation(summary = "Получение списка стран", security = @SecurityRequirement(name = "jwt"))
    @PreAuthorize("hasAuthority('user:write')")
    @GetMapping("/countries")
    public ResponseEntity<StringListDataResponse> getCountries() throws IOException, SAXException {
        return new ResponseEntity<>(locationService.getCountries(), HttpStatus.OK);
    }

    @Operation(summary = "Получение списка городов по запрашиваемой стране", security = @SecurityRequirement(name = "jwt"))
    @PreAuthorize("hasAuthority('user:write')")
    @GetMapping("/cities")
    public ResponseEntity<StringListDataResponse> getCities(@RequestParam("country") String country) throws IOException, SAXException {
        return new ResponseEntity<>(locationService.getCities(country), HttpStatus.OK);
    }
}
