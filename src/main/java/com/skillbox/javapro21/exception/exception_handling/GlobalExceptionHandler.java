package com.skillbox.javapro21.exception.exception_handling;

import com.skillbox.javapro21.api.response.BadDataResponse;
import com.skillbox.javapro21.exception.*;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    ResponseEntity<BadDataResponse> handleIncorrectCaptchaException(CaptchaCodeException exception) {
        BadDataResponse badDataResponse = new BadDataResponse().setError("captcha").setDescription(exception.getMessage());
        return new ResponseEntity<>(badDataResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    ResponseEntity<BadDataResponse> handleUserExistException(UserExistException exception) {
        BadDataResponse badDataResponse = new BadDataResponse().setError("email").setDescription(exception.getMessage());
        return new ResponseEntity<>(badDataResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    ResponseEntity<BadDataResponse> handleParseWeatherException(ParseException exception) {
        BadDataResponse badDataResponse =
                new BadDataResponse().setError("weather_parse").setDescription("Не удалось получить данные о погоде");
        return new ResponseEntity<>(badDataResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    ResponseEntity<BadDataResponse> handleCheatingException(CheatingException exception) {
        return new ResponseEntity<>(new BadDataResponse()
                                        .setError("likes")
                                        .setDescription(exception.getMessage()),
                                        HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    ResponseEntity<BadDataResponse> handleBadArgumentException(BadArgumentException exception) {
        return new ResponseEntity<>(new BadDataResponse()
                                        .setError("likes")
                                        .setDescription(exception.getMessage()),
                                        HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    ResponseEntity<BadDataResponse> handlePostNotFoundException(PostNotFoundException exception) {
        return new ResponseEntity<>(new BadDataResponse()
                                        .setError("post")
                                        .setDescription(exception.getMessage()),
                                        HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    ResponseEntity<BadDataResponse> handlePostCommentNotFoundException(PostCommentNotFoundException exception) {
        return new ResponseEntity<>(new BadDataResponse()
                                        .setError("comment")
                                        .setDescription(exception.getMessage()),
                                        HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    ResponseEntity<BadDataResponse> handlePostLikeNotFoundException(PostLikeNotFoundException exception) {
        return new ResponseEntity<>(new BadDataResponse()
                                        .setError("like")
                                        .setDescription(exception.getMessage()),
                                        HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    ResponseEntity<BadDataResponse> handleCommentLikeNotFoundException(CommentLikeNotFoundException exception) {
        return new ResponseEntity<>(new BadDataResponse()
                                        .setError("like")
                                        .setDescription(exception.getMessage()),
                                        HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    ResponseEntity<BadDataResponse> handlePersonNotFoundException(PersonNotFoundException exception) {
        return new ResponseEntity<>(new BadDataResponse()
                                        .setError("user")
                                        .setDescription(exception.getMessage()),
                                        HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    ResponseEntity<BadDataResponse> handleInterlockedFriendshipStatusException(InterlockedFriendshipStatusException exception) {
        return new ResponseEntity<>(new BadDataResponse()
                                        .setError("friendship")
                                        .setDescription(exception.getMessage()),
                                        HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    ResponseEntity<BadDataResponse> handleBlockPersonHimselfException(BlockPersonHimselfException exception) {
        return new ResponseEntity<>(new BadDataResponse()
                                        .setError("friendship")
                                        .setDescription(exception.getMessage()),
                                        HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    ResponseEntity<BadDataResponse> handleFriendshipNotFoundException(FriendshipNotFoundException exception) {
        return new ResponseEntity<>(new BadDataResponse()
                                        .setError("friendship")
                                        .setDescription(exception.getMessage()),
                                        HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    ResponseEntity<BadDataResponse> handleNonBlockedFriendshipException(NonBlockedFriendshipException exception) {
        return new ResponseEntity<>(new BadDataResponse()
                                        .setError("friendship")
                                        .setDescription(exception.getMessage()),
                                        HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    ResponseEntity<BadDataResponse> handleAuthorAndUserEqualsException(AuthorAndUserEqualsException exception) {
        return new ResponseEntity<>(new BadDataResponse()
                                        .setError("user")
                                        .setDescription(exception.getMessage()),
                                        HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    ResponseEntity<BadDataResponse> handlePostRecoveryException(PostRecoveryException exception) {
        return new ResponseEntity<>(new BadDataResponse()
                                        .setError("post")
                                        .setDescription(exception.getMessage()),
                                        HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    ResponseEntity<BadDataResponse> handleCommentNotFoundException(CommentNotFoundException exception) {
        return new ResponseEntity<>(new BadDataResponse()
                                        .setError("comment")
                                        .setDescription(exception.getMessage()),
                                        HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    ResponseEntity<BadDataResponse> handleCommentNotAuthorException(CommentNotAuthorException exception) {
        return new ResponseEntity<>(new BadDataResponse()
                                        .setError("comment")
                                        .setDescription(exception.getMessage()),
                                        HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    ResponseEntity<BadDataResponse> handleMessageNotFoundException(MessageNotFoundException exception) {
        return new ResponseEntity<>(new BadDataResponse()
                                        .setError("message")
                                        .setDescription(exception.getMessage()),
                                        HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    ResponseEntity<BadDataResponse> handleNotFoundException(NotFoundException exception) {
        return new ResponseEntity<>(new BadDataResponse()
                                        .setError("not found")
                                        .setDescription(exception.getMessage()),
                                        HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    ResponseEntity<BadDataResponse> handleNotSuchUserOrWrongPasswordException(NotSuchUserOrWrongPasswordException exception) {
        return new ResponseEntity<>(new BadDataResponse()
                                        .setError("user")
                                        .setDescription(exception.getMessage()),
                                        HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    ResponseEntity<BadDataResponse> handleTokenConfirmationException(TokenConfirmationException exception) {
        return new ResponseEntity<>(new BadDataResponse()
                                        .setError("token")
                                        .setDescription(exception.getMessage()),
                                        HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    ResponseEntity<BadDataResponse> handleUnauthorizedUserException(UnauthorizedUserException exception) {
        return new ResponseEntity<>(new BadDataResponse()
                                        .setError("user")
                                        .setDescription(exception.getMessage()),
                                        HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler
    ResponseEntity<BadDataResponse> handleUserExistOnDialogException(UserExistOnDialogException exception) {
        return new ResponseEntity<>(new BadDataResponse()
                                        .setError("dialog")
                                        .setDescription(exception.getMessage()),
                                        HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    ResponseEntity<BadDataResponse> handleUserLegalException(UserLegalException exception) {
        return new ResponseEntity<>(new BadDataResponse()
                                        .setError("user")
                                        .setDescription(exception.getMessage()),
                                        HttpStatus.BAD_REQUEST);
    }
}
