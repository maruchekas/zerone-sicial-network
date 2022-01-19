package com.skillbox.javapro21.service;

import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.tag.TagData;
import org.springframework.stereotype.Service;

@Service
public interface TagService {

    ListDataResponse<TagData> getTags(String tagRequest, int offset, int itemPerPage);
}
