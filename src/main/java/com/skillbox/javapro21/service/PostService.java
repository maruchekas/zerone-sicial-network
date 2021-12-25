package com.skillbox.javapro21.service;

import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.domain.Post;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public interface PostService {
    ListDataResponse<Post> getPosts(String text, long dateFrom, long dateTo, int offset, int itemPerPage, Principal principal);
}
