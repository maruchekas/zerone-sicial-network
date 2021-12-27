package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Slf4j
@RestController
@Tag(name = "Контроллер для работы с постами")
@RequestMapping("/api/v1")
public class PostController {
    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @Operation(summary = "Поиск публикации")
    @GetMapping("/post")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ListDataResponse<?>> getPosts(@RequestParam(name = "text", defaultValue = "") String text,
                                                           @RequestParam(name = "date_from", defaultValue = "-1") long dateFrom,
                                                           @RequestParam(name = "date_to", defaultValue = "-1") long dateTo,
                                                           @RequestParam(name = "offset", defaultValue = "0") int offset,
                                                           @RequestParam(name = "item_per_page", defaultValue = "20") int itemPerPage,
                                                           @RequestParam(name = "author", defaultValue = "") String author,
                                                           @RequestParam(name = "tag", defaultValue = "") String tag,
                                                           Principal principal) {
        return new ResponseEntity<>(postService.getPosts(text, dateFrom, dateTo, offset, itemPerPage, author, tag, principal), HttpStatus.OK);
    }
}
