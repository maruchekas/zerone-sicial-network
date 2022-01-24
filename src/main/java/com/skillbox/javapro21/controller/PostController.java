package com.skillbox.javapro21.controller;

import com.mailjet.client.errors.MailjetException;
import com.skillbox.javapro21.aop.LastActivity;
import com.skillbox.javapro21.api.request.post.CommentRequest;
import com.skillbox.javapro21.api.request.post.PostRequest;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.MessageOkContent;
import com.skillbox.javapro21.api.response.post.CommentDelete;
import com.skillbox.javapro21.api.response.post.CommentsData;
import com.skillbox.javapro21.api.response.post.PostData;
import com.skillbox.javapro21.api.response.post.PostDeleteResponse;
import com.skillbox.javapro21.exception.*;
import com.skillbox.javapro21.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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

    @Operation(summary = "Поиск публикации", security = @SecurityRequirement(name = "jwt"))
    @GetMapping("/post")
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
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

    @Operation(summary = "Поиск публикации по id", security = @SecurityRequirement(name = "jwt"))
    @GetMapping("/post/{id}")
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<DataResponse<PostData>> getPostById(@PathVariable Long id,
                                                               Principal principal) throws PostNotFoundException {
        return new ResponseEntity<>(postService.getPostById(id, principal), HttpStatus.OK);
    }

    @Operation(summary = "Изменение публикации по id и отложенная публикация", security = @SecurityRequirement(name = "jwt"))
    @PutMapping("/post/{id}")
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<DataResponse<PostData>> putPostByIdAndMessageInDay(@PathVariable Long id,
                                                                             @RequestParam(name = "publish_date", defaultValue = "-1") long publishDate,
                                                                             @RequestBody PostRequest postRequest,
                                                                             Principal principal) throws PostNotFoundException, AuthorAndUserEqualsException {
        return new ResponseEntity<>(postService.putPostByIdAndMessageInDay(id, publishDate, postRequest, principal), HttpStatus.OK);
    }

    @Operation(summary = "Удаление публикации по id", security = @SecurityRequirement(name = "jwt"))
    @DeleteMapping("/post/{id}")
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<DataResponse<PostDeleteResponse>> putPostByIdAndMessageInDay(@PathVariable Long id,
                                                                                       Principal principal) throws PostNotFoundException, AuthorAndUserEqualsException {
        return new ResponseEntity<>(postService.deletePostById(id, principal), HttpStatus.OK);
    }

    @Operation(summary = "Восстановление публикации по id", security = @SecurityRequirement(name = "jwt"))
    @PutMapping("/post/{id}/recover")
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<DataResponse<PostData>> recoverPostById(@PathVariable Long id,
                                                                  Principal principal) throws PostNotFoundException, AuthorAndUserEqualsException, PostRecoveryException {
        return new ResponseEntity<>(postService.recoverPostById(id, principal), HttpStatus.OK);
    }

    @Operation(summary = "Получение комментариев к посту", security = @SecurityRequirement(name = "jwt"))
    @GetMapping("/post/{id}/comments")
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<ListDataResponse<CommentsData>> getComments(@PathVariable Long id,
                                                                      @RequestParam(name = "offset", defaultValue = "0") int offset,
                                                                      @RequestParam(name = "item_per_page", defaultValue = "5") int itemPerPage,
                                                                      Principal principal) throws PostNotFoundException {
        return new ResponseEntity<>(postService.getComments(id, offset, itemPerPage, principal), HttpStatus.OK);
    }

    @Operation(summary = "Добавление комментариев к посту", security = @SecurityRequirement(name = "jwt"))
    @PostMapping("/post/{id}/comments")
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<DataResponse<CommentsData>> postComments(@PathVariable Long id,
                                                                       @RequestBody CommentRequest commentRequest,
                                                                       Principal principal) throws PostNotFoundException, CommentNotFoundException {
        return new ResponseEntity<>(postService.postComments(id, commentRequest, principal), HttpStatus.OK);
    }

    @Operation(summary = "Редактирование комментария к посту", security = @SecurityRequirement(name = "jwt"))
    @PutMapping("/post/{id}/comments/{comment_id}")
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<DataResponse<CommentsData>> putComments(@PathVariable Long id,
                                                                   @PathVariable(name = "comment_id") Long commentId,
                                                                   @RequestBody CommentRequest commentRequest,
                                                                   Principal principal) throws PostNotFoundException, CommentNotFoundException, CommentNotAuthorException {
        return new ResponseEntity<>(postService.putComments(id, commentId, commentRequest, principal), HttpStatus.OK);
    }

    @Operation(summary = "Удаление комментария", security = @SecurityRequirement(name = "jwt"))
    @DeleteMapping("/post/{id}/comments/{comment_id}")
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<DataResponse<CommentDelete>> deleteComments(@PathVariable Long id,
                                                                      @PathVariable(name = "comment_id") Long commentId,
                                                                      Principal principal) throws PostNotFoundException, CommentNotFoundException, CommentNotAuthorException {
        return new ResponseEntity<>(postService.deleteComments(id, commentId, principal), HttpStatus.OK);
    }

    @Operation(summary = "Восстановление комментария", security = @SecurityRequirement(name = "jwt"))
    @PutMapping("/post/{id}/comments/{comment_id}/recover")
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<DataResponse<CommentsData>> recoverComments(@PathVariable Long id,
                                                                      @PathVariable(name = "comment_id") Long commentId,
                                                                      Principal principal) throws PostNotFoundException, CommentNotFoundException, CommentNotAuthorException {
        return new ResponseEntity<>(postService.recoverComments(id, commentId, principal), HttpStatus.OK);
    }

    @Operation(summary = "Жалоба на пост", security = @SecurityRequirement(name = "jwt"))
    @PostMapping("/post/{id}/report")
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<DataResponse<MessageOkContent>> ratPostController(@PathVariable Long id,
                                                                        Principal principal) throws PostNotFoundException, CommentNotFoundException, CommentNotAuthorException, MailjetException, IOException {
        return new ResponseEntity<>(postService.ratPostController(id, principal), HttpStatus.OK);
    }

    @Operation(summary = "Жалоба на комментарий к посту", security = @SecurityRequirement(name = "jwt"))
    @PostMapping("/post/{id}/comments/{comment_id}/report")
    @PreAuthorize("hasAuthority('user:write')")
    @LastActivity
    public ResponseEntity<DataResponse<MessageOkContent>> ratCommentController(@PathVariable Long id,
                                                                               @PathVariable(name = "comment_id") Long commentId,
                                                                        Principal principal) throws PostNotFoundException, CommentNotFoundException, CommentNotAuthorException, MailjetException, IOException {
        return new ResponseEntity<>(postService.ratCommentController(id, commentId, principal), HttpStatus.OK);
    }
}
