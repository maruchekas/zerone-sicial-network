package com.skillbox.javapro21.service;

import com.mailjet.client.errors.MailjetException;
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
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.Principal;

@Service
public interface PostService {
    ListDataResponse<PostData> getPosts(String text, long dateFrom, long dateTo, int offset, int itemPerPage, String author, String[] tags, Principal principal);

    DataResponse<PostData> getPostById(Long id, Principal principal) throws PostNotFoundException;

    DataResponse<PostData> putPostByIdAndMessageInDay(Long id, long publishDate, PostRequest postRequest, Principal principal) throws PostNotFoundException, AuthorAndUserEqualsException;

    DataResponse<PostDeleteResponse> deletePostById(Long id, Principal principal) throws PostNotFoundException, AuthorAndUserEqualsException;

    DataResponse<PostData> recoverPostById(Long id, Principal principal) throws PostNotFoundException, AuthorAndUserEqualsException, PostRecoveryException;

    ListDataResponse<CommentsData> getComments(Long id, int offset, int itemPerPage, Principal principal) throws PostNotFoundException;

    DataResponse<CommentsData> postComments(Long id, CommentRequest commentRequest, Principal principal) throws PostNotFoundException, CommentNotFoundException;

    DataResponse<CommentsData> putComments(Long id, Long commentId, CommentRequest commentRequest, Principal principal) throws PostNotFoundException, CommentNotFoundException, CommentNotAuthorException;

    DataResponse<CommentDelete> deleteComments(Long id, Long commentId, Principal principal) throws CommentNotFoundException, CommentNotAuthorException;

    DataResponse<CommentsData> recoverComments(Long id, Long commentId, Principal principal) throws CommentNotFoundException, CommentNotAuthorException;

    DataResponse<MessageOkContent> ratPostController(Long id, Principal principal) throws PostNotFoundException, MailjetException, IOException;

    DataResponse<MessageOkContent> ratCommentController(Long id, Long commentId, Principal principal) throws CommentNotFoundException, MailjetException, IOException;

    ListDataResponse<PostData> getFeeds(String name, int offset, int itemPerPage, Principal principal);
}
