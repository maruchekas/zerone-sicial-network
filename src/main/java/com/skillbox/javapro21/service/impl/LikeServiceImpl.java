package com.skillbox.javapro21.service.impl;

import com.skillbox.javapro21.api.request.like.LikeRequest;
import com.skillbox.javapro21.api.response.Content;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.like.LikeBoolean;
import com.skillbox.javapro21.api.response.like.LikeData;
import com.skillbox.javapro21.domain.*;
import com.skillbox.javapro21.domain.marker.HavePerson;
import com.skillbox.javapro21.exception.*;
import com.skillbox.javapro21.repository.CommentLikeRepository;
import com.skillbox.javapro21.repository.PostCommentRepository;
import com.skillbox.javapro21.repository.PostLikeRepository;
import com.skillbox.javapro21.repository.PostRepository;
import com.skillbox.javapro21.service.LikeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LikeServiceImpl implements LikeService{

    private static final String POST = "Post";
    private static final String COMMENT = "Comment";
    private static final String UNEXPECTED = "Unexpected value: ";
    private static final String DOES_NOT_EXIST = " не существует";

    private final UtilsService utilsService;
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final PostCommentRepository postCommentRepository;

    @Autowired
    public LikeServiceImpl(UtilsService utilsService, PostLikeRepository postLikeRepository, PostRepository postRepository, CommentLikeRepository commentLikeRepository, PostCommentRepository postCommentRepository) {
        this.utilsService = utilsService;
        this.postLikeRepository = postLikeRepository;
        this.postRepository = postRepository;
        this.commentLikeRepository = commentLikeRepository;
        this.postCommentRepository = postCommentRepository;
    }

    @Override
    public DataResponse<Content> isLiked(LikeRequest request, Principal principal) throws BadArgumentException {
        Person currentUser = utilsService.findPersonByEmail(principal.getName());

        switch (request.getType()) {
            case POST -> {
                Optional<PostLike> possiblePostLike = postLikeRepository.findByPersonIdAndPostId(currentUser.getId(), request.getItemId());
                return utilsService.getDataResponse(createLikeBooleanData(possiblePostLike));
            }
            case COMMENT -> {
                Optional<CommentLike> possibleCommentLike = commentLikeRepository.findByPersonIdAndCommentId(currentUser.getId(), request.getItemId());
                return utilsService.getDataResponse(createLikeBooleanData(possibleCommentLike));
            }
            default -> throw new BadArgumentException(UNEXPECTED + request.getType());
        }
    }

    @Override
    public DataResponse<Content> getLikes(LikeRequest request) throws BadArgumentException, CommentLikeNotFoundException, PostLikeNotFoundException {
        switch (request.getType()) {
            case POST -> {
                List<PostLike> likes = postLikeRepository.findPostLikeByPostId(request.getItemId());
                if (likes.isEmpty()) {
                    throw new PostLikeNotFoundException("Пост с id = " + request.getItemId() + " не имеет лайков");
                }
                return utilsService.getDataResponse(createLikeData(likes));
            }
            case COMMENT -> {
                List<CommentLike> likes = commentLikeRepository.findAllByCommentId(request.getItemId());
                if (likes.isEmpty()) {
                    throw new CommentLikeNotFoundException("Комментарий с id = " + request.getItemId() + " не имеет лайков");
                }
                return utilsService.getDataResponse(createLikeData(likes));
            }
            default -> throw new BadArgumentException(UNEXPECTED + request.getType());
        }

    }

    @Override
    public DataResponse<Content> putLike(LikeRequest request, Principal principal) throws PostNotFoundException, PostCommentNotFoundException, BadArgumentException, PostLikeNotFoundException, CommentLikeNotFoundException {
        Person currentUser = utilsService.findPersonByEmail(principal.getName());

        switch (request.getType()) {
            case POST -> {
                Optional<Post> possiblePost = postRepository.findPostById(request.getItemId());
                if (possiblePost.isEmpty()) {
                    throw new PostNotFoundException("Поста с id = " + request.getItemId() + DOES_NOT_EXIST);
                }
                postLikeRepository.save(new PostLike()
                                            .setTime(LocalDateTime.now())
                                            .setPerson(currentUser)
                                            .setPost(possiblePost.get()));
                return getLikes(request);
            }
            case COMMENT -> {
                Optional<PostComment> possibleComment = postCommentRepository.findById(request.getItemId());
                if (possibleComment.isEmpty()) {
                    throw new PostCommentNotFoundException("Комментария с id = " + request.getItemId() + DOES_NOT_EXIST);
                }
                commentLikeRepository.save(new CommentLike()
                                                .setTime(LocalDateTime.now())
                                                .setPerson(currentUser)
                                                .setComment(possibleComment.get()));
                return getLikes(request);
            }
            default -> throw new BadArgumentException(UNEXPECTED + request.getType());
        }
    }

    @Override
    public DataResponse<Content> deleteLike(LikeRequest request, Principal principal) throws PostLikeNotFoundException, CommentLikeNotFoundException, BadArgumentException {
        Person currentUser = utilsService.findPersonByEmail(principal.getName());

        switch (request.getType()) {
            case POST -> {
                Optional<PostLike> possiblePostLike = postLikeRepository.findByPersonIdAndPostId(currentUser.getId(), request.getItemId());
                if (possiblePostLike.isEmpty()) {
                    throw new PostLikeNotFoundException("Лайка на пост с id = " + request.getItemId() + " от пользователя " + currentUser.getEmail() + DOES_NOT_EXIST);
                }
                postLikeRepository.delete(possiblePostLike.get());
                return getLikes(request);
            }
            case COMMENT -> {
                Optional<CommentLike> possibleCommentLike = commentLikeRepository.findByPersonIdAndCommentId(currentUser.getId(), request.getItemId());
                if (possibleCommentLike.isEmpty()) {
                    throw new CommentLikeNotFoundException("Лайка на комментарий с id = " + request.getItemId() + " от пользователя " + currentUser.getEmail() + DOES_NOT_EXIST);
                }
                commentLikeRepository.delete(possibleCommentLike.get());
                return getLikes(request);
            }
            default -> throw new BadArgumentException(UNEXPECTED + request.getType());
        }
    }


    private <T> LikeBoolean createLikeBooleanData(Optional<T> possibleEntity) {
        LikeBoolean data = new LikeBoolean();
        data.setLikes(possibleEntity.isPresent());
        return data;
    }

    private <T extends HavePerson> LikeData createLikeData(List<T> likes) {
        return new LikeData()
                .setLikes(String.valueOf(likes.size()))
                .setUsers(likes.stream()
                        .map(l -> String.valueOf(l.getPerson().getId()))
                        .collect(Collectors.toList()));
    }
}
