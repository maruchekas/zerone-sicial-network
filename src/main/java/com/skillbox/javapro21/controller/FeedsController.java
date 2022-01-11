package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.aop.LastActivity;
import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.post.PostData;
import com.skillbox.javapro21.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@Tag(name = "Работа с лентой новостей")
@RequestMapping("/api/v1/feeds")
@RequiredArgsConstructor
public class FeedsController {
    private final PostService postService;

    @Operation(summary = "Поиск публикации", security = @SecurityRequirement(name = "jwt"))
    @GetMapping("")
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<ListDataResponse<PostData>> getPosts(@RequestParam(name = "name", defaultValue = "") String name,
                                                               @RequestParam(name = "offset", defaultValue = "0") int offset,
                                                               @RequestParam(name = "item_per_page", defaultValue = "20") int itemPerPage,
                                                               Principal principal) {
        return new ResponseEntity<>(postService.getFeeds(name, offset, itemPerPage, principal), HttpStatus.OK);
    }

}
