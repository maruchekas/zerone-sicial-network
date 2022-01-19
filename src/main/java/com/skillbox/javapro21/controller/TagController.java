package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.tag.TagData;
import com.skillbox.javapro21.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class TagController {

    private final TagService tagService;

    @GetMapping("/tags")
    ResponseEntity<ListDataResponse<TagData>> getTags(
            @RequestParam(name = "tag", defaultValue = "") String tag,
            @RequestParam(name = "offset", defaultValue = "0") int offset,
            @RequestParam(name = "item_per_page", defaultValue = "20") int itemPerPage) {
        return new ResponseEntity<>(tagService.getTags(tag, offset, itemPerPage), HttpStatus.OK);
    }
}
