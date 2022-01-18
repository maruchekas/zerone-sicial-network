package com.skillbox.javapro21.service;

import com.skillbox.javapro21.api.request.like.LikeRequest;
import com.skillbox.javapro21.api.response.Content;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.exception.*;

import java.security.Principal;

public interface LikeService {

    DataResponse<Content> isLiked(LikeRequest request, Principal principal) throws BadArgumentException;
    DataResponse<Content> getLikes(LikeRequest request) throws BadArgumentException, CommentLikeNotFoundException, PostLikeNotFoundException;
    DataResponse<Content> putLike(LikeRequest request, Principal principal) throws PostNotFoundException, PostCommentNotFoundException, BadArgumentException, PostLikeNotFoundException, CommentLikeNotFoundException;
    DataResponse<Content> deleteLike(LikeRequest request, Principal principal) throws PostLikeNotFoundException, CommentLikeNotFoundException, BadArgumentException;
}
