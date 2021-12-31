package com.skillbox.javapro21.service;

import com.skillbox.javapro21.api.request.post.PostRequest;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.post.CommentsData;
import com.skillbox.javapro21.api.response.post.PostData;
import com.skillbox.javapro21.api.response.post.PostDeleteResponse;
import com.skillbox.javapro21.exception.AuthorAndUserEqualsException;
import com.skillbox.javapro21.exception.PostNotFoundException;
import com.skillbox.javapro21.exception.PostRecoveryException;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public interface PostService {
    ListDataResponse<PostData> getPosts(String text, long dateFrom, long dateTo, int offset, int itemPerPage, String author, String tag, Principal principal);

    DataResponse<PostData> getPostsById(Long id, Principal principal) throws PostNotFoundException;

    DataResponse<PostData> putPostByIdAndMessageInDay(Long id, long publishDate, PostRequest postRequest, Principal principal) throws PostNotFoundException, AuthorAndUserEqualsException;

    DataResponse<PostDeleteResponse> deletePostById(Long id, Principal principal) throws PostNotFoundException, AuthorAndUserEqualsException;

    DataResponse<PostData> recoverPostById(Long id, Principal principal) throws PostNotFoundException, AuthorAndUserEqualsException, PostRecoveryException;

    ListDataResponse<CommentsData> getComments(Long id, int offset, int itemPerPage, Principal principal) throws PostNotFoundException;
}
