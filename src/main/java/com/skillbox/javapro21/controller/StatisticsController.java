package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.aop.LastActivity;
import com.skillbox.javapro21.api.response.statistics.*;
import com.skillbox.javapro21.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Контроллер для работы со статистикой")
@RequestMapping("/api/v1/stat")
@RequiredArgsConstructor
public class StatisticsController {
    private final StatisticsService statisticsService;

    @Operation(summary = "Общая статистика", security = @SecurityRequirement(name = "jwt"))
    @GetMapping("/all")
    @LastActivity
    @PreAuthorize("hasAuthority('user:administrate')")
    public ResponseEntity<StatisticsResponse> getAllStatistic() {
        return new ResponseEntity<>(statisticsService.getAllStatistic(), HttpStatus.OK);
    }

    @Operation(summary = "Статистика по постам", security = @SecurityRequirement(name = "jwt"))
    @GetMapping("/posts")
    @LastActivity
    @PreAuthorize("hasAuthority('user:administrate')")
    public ResponseEntity<PostStatResponse> getPostsStatistic() {
        return new ResponseEntity<>(statisticsService.getPostsStatistic(), HttpStatus.OK);
    }

    @Operation(summary = "Статистика по пользователям", security = @SecurityRequirement(name = "jwt"))
    @GetMapping("/users")
    @LastActivity
    @PreAuthorize("hasAuthority('user:administrate')")
    public ResponseEntity<UsersStatResponse> getUsersStatistic() {
        return new ResponseEntity<>(statisticsService.getUsersStatistic(), HttpStatus.OK);
    }

    @Operation(summary = "Статистика по комментариям", security = @SecurityRequirement(name = "jwt"))
    @GetMapping("/comments")
    @LastActivity
    @PreAuthorize("hasAuthority('user:administrate')")
    public ResponseEntity<CommentsStatResponse> getCommentsStatistic() {
        return new ResponseEntity<>(statisticsService.getCommentsStatistic(), HttpStatus.OK);
    }

    @Operation(summary = "Статистика по лайкам", security = @SecurityRequirement(name = "jwt"))
    @GetMapping("/likes")
    @LastActivity
    @PreAuthorize("hasAuthority('user:administrate')")
    public ResponseEntity<LikesStatResponse> getLikesStatistic() {
        return new ResponseEntity<>(statisticsService.getLikesStatistic(), HttpStatus.OK);
    }
}
