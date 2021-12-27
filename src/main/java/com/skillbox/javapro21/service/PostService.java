package com.skillbox.javapro21.service;

import com.skillbox.javapro21.api.response.ListDataResponse;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public interface PostService {
    ListDataResponse<?> getPosts(String text, long dateFrom, long dateTo, int offset, int itemPerPage, String author, String tag, Principal principal);
}
