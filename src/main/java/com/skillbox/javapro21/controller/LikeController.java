package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.aop.LastActivity;
import com.skillbox.javapro21.api.request.like.LikeRequest;
import com.skillbox.javapro21.api.response.Content;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.exception.CustomException;
import com.skillbox.javapro21.service.impl.LikeServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequiredArgsConstructor
@RestController
@Tag(name = "Контроллер для работы с лайками")
@RequestMapping("/api/v1")
public class LikeController {

    private final LikeServiceImpl likeService;


    @Operation(summary = "Был ли поставлен лайк пользователем на пост/комментарий", security = @SecurityRequirement(name = "jwt"))
    @LastActivity
    @PreAuthorize("hasAuthority('user:write')")
    @GetMapping("/liked")
    public ResponseEntity<DataResponse<Content>> isLiked(@RequestBody LikeRequest request,
                                                         Principal principal) throws CustomException {
        return new ResponseEntity<>(likeService.isLiked(request, principal), HttpStatus.OK);
    }

    @Operation(summary = "Получить список лайков на пост/комментарий", security = @SecurityRequirement(name = "jwt"))
    @LastActivity
    @PreAuthorize("hasAuthority('user:write')")
    @GetMapping("/likes")
    public ResponseEntity<DataResponse<Content>> getLikes(@RequestBody LikeRequest request) throws CustomException {
        return new ResponseEntity<>(likeService.getLikes(request), HttpStatus.OK);
    }

    @Operation(summary = "Пользователь ставит лайк на пост/комментарий", security = @SecurityRequirement(name = "jwt"))
    @LastActivity
    @PreAuthorize("hasAuthority('user:write')")
    @PutMapping("/likes")
    public ResponseEntity<DataResponse<Content>> putLike(@RequestBody LikeRequest request,
                                                         Principal principal) throws CustomException {
        return new ResponseEntity<>(likeService.putLike(request, principal), HttpStatus.OK);
    }

    @Operation(summary = "Пользователь убирает лайк с поста/комментария", security = @SecurityRequirement(name = "jwt"))
    @LastActivity
    @PreAuthorize("hasAuthority('user:write')")
    @DeleteMapping("/likes")
    public ResponseEntity<DataResponse<Content>> deleteLike(@RequestBody LikeRequest request,
                                                            Principal principal) throws CustomException {
        return new ResponseEntity<>(likeService.deleteLike(request, principal), HttpStatus.OK);
    }
}
