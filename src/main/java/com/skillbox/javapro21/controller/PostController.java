package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.api.request.post.PostRequest;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.post.CommentsData;
import com.skillbox.javapro21.api.response.post.PostData;
import com.skillbox.javapro21.api.response.post.PostDeleteResponse;
import com.skillbox.javapro21.exception.AuthorAndUserEqualsException;
import com.skillbox.javapro21.exception.PostNotFoundException;
import com.skillbox.javapro21.exception.PostRecoveryException;
import com.skillbox.javapro21.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ListDataResponse<PostData>> getPosts(@RequestParam(name = "text", defaultValue = "") String text,
                                                               @RequestParam(name = "date_from", defaultValue = "-1") long dateFrom,
                                                               @RequestParam(name = "date_to", defaultValue = "-1") long dateTo,
                                                               @RequestParam(name = "offset", defaultValue = "0") int offset,
                                                               @RequestParam(name = "item_per_page", defaultValue = "20") int itemPerPage,
                                                               @RequestParam(name = "author", defaultValue = "") String author,
                                                               @RequestParam(name = "tag", defaultValue = "") String tag,
                                                               Principal principal) {
        return new ResponseEntity<>(postService.getPosts(text, dateFrom, dateTo, offset, itemPerPage, author, tag, principal), HttpStatus.OK);
    }

    @Operation(summary = "Поиск публикации по id")
    @GetMapping("/post/{id}")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<DataResponse<PostData>> getPostsById(@PathVariable Long id,
                                                               Principal principal) throws PostNotFoundException {
        return new ResponseEntity<>(postService.getPostsById(id, principal), HttpStatus.OK);
    }

    @Operation(summary = "Изменение публикации по id и отложенная публикация")
    @PutMapping("/post/{id}")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<DataResponse<PostData>> putPostByIdAndMessageInDay(@PathVariable Long id,
                                                                             @RequestParam(name = "publish_date", defaultValue = "-1") long publishDate,
                                                                             @RequestBody PostRequest postRequest,
                                                                             Principal principal) throws PostNotFoundException, AuthorAndUserEqualsException {
        return new ResponseEntity<>(postService.putPostByIdAndMessageInDay(id, publishDate, postRequest, principal), HttpStatus.OK);
    }

    @Operation(summary = "Удаление публикации по id")
    @DeleteMapping("/post/{id}")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<DataResponse<PostDeleteResponse>> putPostByIdAndMessageInDay(@PathVariable Long id,
                                                                                       Principal principal) throws PostNotFoundException, AuthorAndUserEqualsException {
        return new ResponseEntity<>(postService.deletePostById(id, principal), HttpStatus.OK);
    }

    @Operation(summary = "Восстановление публикации по id")
    @PutMapping("/post/{id}/recover")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<DataResponse<PostData>> recoverPostById(@PathVariable Long id,
                                                                  Principal principal) throws PostNotFoundException, AuthorAndUserEqualsException, PostRecoveryException {
        return new ResponseEntity<>(postService.recoverPostById(id, principal), HttpStatus.OK);
    }

    @Operation(summary = "Получение комментариев к посту")
    @GetMapping("/post/{id/comments}")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ListDataResponse<CommentsData>> getComments(@PathVariable Long id,
                                                                      @RequestParam(name = "offset", defaultValue = "0") int offset,
                                                                      @RequestParam(name = "item_per_page", defaultValue = "20") int itemPerPage,
                                                                      Principal principal) {
        return new ResponseEntity<>(postService.getComments(id, offset, itemPerPage, principal), HttpStatus.OK);
    }
}
