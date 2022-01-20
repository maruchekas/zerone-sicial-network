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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
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
    public DataResponse<Content> getLikes(LikeRequest request) throws BadArgumentException, PostNotFoundException, PostCommentNotFoundException {
        switch (request.getType()) {
            case POST -> {
                List<PostLike> likes = postLikeRepository.findPostLikeByPostId(getPost(request.getItemId()).getId());
                return utilsService.getDataResponse(createLikeData(likes));
            }
            case COMMENT -> {
                List<CommentLike> likes = commentLikeRepository.findAllByCommentId(getComment(request.getItemId()).getId());
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
                postLikeRepository.save(new PostLike()
                                            .setTime(LocalDateTime.now())
                                            .setPerson(currentUser)
                                            .setPost(getPost(request.getItemId())));
                return getLikes(request);
            }
            case COMMENT -> {
                commentLikeRepository.save(new CommentLike()
                                                .setTime(LocalDateTime.now())
                                                .setPerson(currentUser)
                                                .setComment(getComment(request.getItemId())));
                return getLikes(request);
            }
            default -> throw new BadArgumentException(UNEXPECTED + request.getType());
        }
    }

    @Override
    public DataResponse<Content> deleteLike(LikeRequest request, Principal principal) throws PostLikeNotFoundException, CommentLikeNotFoundException, BadArgumentException, PostNotFoundException, PostCommentNotFoundException {
        Person currentUser = utilsService.findPersonByEmail(principal.getName());

        switch (request.getType()) {
            case POST -> {
                postLikeRepository.delete(getPostLike(currentUser, request.getItemId()));
                return getLikes(request);
            }
            case COMMENT -> {
                commentLikeRepository.delete(getCommentLike(currentUser, request.getItemId()));
                return getLikes(request);
            }
            default -> throw new BadArgumentException(UNEXPECTED + request.getType());
        }
    }


    private Post getPost(Long id) throws PostNotFoundException {
        Optional<Post> possiblePost = postRepository.findPostById(id);
        if (possiblePost.isEmpty()) {
            throw new PostNotFoundException("Поста с id = " + id + DOES_NOT_EXIST);
        }
        return possiblePost.get();
    }

    private PostComment getComment(Long id) throws PostCommentNotFoundException {
        Optional<PostComment> possibleComment = postCommentRepository.findById(id);
        if (possibleComment.isEmpty()) {
            throw new PostCommentNotFoundException("Комментария с id = " + id + DOES_NOT_EXIST);
        }
        return possibleComment.get();
    }

    private PostLike getPostLike(Person person, Long postId) throws PostLikeNotFoundException {
        Optional<PostLike> possiblePostLike = postLikeRepository.findByPersonIdAndPostId(person.getId(), postId);
        if (possiblePostLike.isEmpty()) {
            throw new PostLikeNotFoundException("Лайка на пост с id = " + postId + " от пользователя " + person.getEmail() + DOES_NOT_EXIST);
        }
        return possiblePostLike.get();
    }

    private CommentLike getCommentLike(Person person, Long postId) throws CommentLikeNotFoundException {
        Optional<CommentLike> possibleCommentLike = commentLikeRepository.findByPersonIdAndCommentId(person.getId(), postId);
        if (possibleCommentLike.isEmpty()) {
            throw new CommentLikeNotFoundException("Лайка на комментарий с id = " + postId + " от пользователя " + person.getEmail() + DOES_NOT_EXIST);
        }
        return possibleCommentLike.get();
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
