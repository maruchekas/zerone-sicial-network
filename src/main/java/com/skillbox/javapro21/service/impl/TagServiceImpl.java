package com.skillbox.javapro21.service.impl;

import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.tag.TagData;
import com.skillbox.javapro21.domain.Tag;
import com.skillbox.javapro21.exception.BadArgumentException;
import com.skillbox.javapro21.repository.TagRepository;
import com.skillbox.javapro21.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final UtilsService utilsService;

    public ListDataResponse<TagData> getTags(String tag, int offset, int itemPerPage) {

        Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
        Page<Tag> pageableTagList = tagRepository.findAllByTag(tag.toLowerCase(), pageable);

        return getTagResponse(pageableTagList, offset, itemPerPage);
    }

    public DataResponse<TagData> deleteTagById(long id) throws BadArgumentException {
        Tag tag = tagRepository.findById(id).orElseThrow(() -> new BadArgumentException("Тег с id " + id + " не найден"));
        TagData tagData = new TagData()
                .setId(tag.getId())
                .setTag(tag.getTag());
        tagRepository.delete(tag);

        return utilsService.getDataResponse(tagData);
    }

    private ListDataResponse<TagData> getTagResponse(Page<Tag> tags, int offset, int itemPerPage){
        ListDataResponse<TagData> listDataResponse = new ListDataResponse<>();
        listDataResponse.setError("ok");
        listDataResponse.setTimestamp(UtilsService.getTimestampFromLocalDateTime(LocalDateTime.now()));
        listDataResponse.setTotal((int) tags.getTotalElements());
        listDataResponse.setOffset(offset);
        listDataResponse.setPerPage(itemPerPage);
        listDataResponse.setData(getTagDataList(tags.toList()));

        return listDataResponse;
    }

    private List<TagData> getTagDataList(List<Tag> tags){
        List<TagData> tagDataList = new ArrayList<>();
        tags.forEach(language ->
            tagDataList.add(getTagData(language))
        );

        return tagDataList;
    }

    private TagData getTagData(Tag tag) {
        return new TagData()
                .setId(tag.getId())
                .setTag(tag.getTag());
    }

}
