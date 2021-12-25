package com.skillbox.javapro21.service;

import com.skillbox.javapro21.api.request.profile.PostRequest;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.exception.PersonNotFoundException;
import org.springframework.stereotype.Service;

@Service
public interface ProfileService {

    public DataResponse getPerson(long id) throws PersonNotFoundException;
    public DataResponse post(long id, long publishDate, PostRequest postRequest) throws PersonNotFoundException;
    public ListDataResponse getWall(long id, int offset, int itemPerPage);
}
