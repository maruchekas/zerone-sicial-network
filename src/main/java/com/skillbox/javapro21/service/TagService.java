package com.skillbox.javapro21.service;

import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.tag.TagData;
import com.skillbox.javapro21.domain.Tag;
import com.skillbox.javapro21.exception.BadArgumentException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public interface TagService {

    ListDataResponse<TagData> getTags(String tagRequest, int offset, int itemPerPage);

    DataResponse<TagData> addTag(String name);

    Set<Tag> addTagsToPost(String[] tagsString);

    DataResponse<TagData> deleteTagById(long id) throws BadArgumentException;
}
