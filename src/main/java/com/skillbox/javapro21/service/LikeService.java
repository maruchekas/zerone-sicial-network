package com.skillbox.javapro21.service;

import com.skillbox.javapro21.api.request.like.LikeRequest;
import com.skillbox.javapro21.api.response.Content;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.exception.CustomException;

import java.security.Principal;

public interface LikeService {

    DataResponse<Content> isLiked(LikeRequest request, Principal principal) throws CustomException;

    DataResponse<Content> getLikes(LikeRequest request) throws CustomException;

    DataResponse<Content> putLike(LikeRequest request, Principal principal) throws CustomException;

    DataResponse<Content> deleteLike(LikeRequest request, Principal principal) throws CustomException;
}
