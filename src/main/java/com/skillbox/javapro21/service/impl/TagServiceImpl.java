package com.skillbox.javapro21.service.impl;

import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.tag.TagData;
import com.skillbox.javapro21.domain.Tag;
import com.skillbox.javapro21.repository.TagRepository;
import com.skillbox.javapro21.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public DataResponse<TagData> addTag(String name){
        DataResponse<TagData> dataResponse = new DataResponse<>();
        TagData tagData = new TagData();
        Tag tag = tagRepository.findByName(name);
        if (tag == null){
            tag = new Tag();
            tag.setTag(name);
            dataResponse.setError("ok");
            dataResponse.setTimestamp(utilsService.getTimestampFromLocalDateTime(LocalDateTime.now()));
            dataResponse.setData(tagData);
            tagRepository.save(tag);
            tagData.setId(tag.getId());
            tagData.setTag(name);
            return dataResponse;
        }
        dataResponse.setError("ok");
        dataResponse.setTimestamp(utilsService.getTimestampFromLocalDateTime(LocalDateTime.now()));
        tagData.setTag(tag.getTag());
        tagData.setId(tag.getId());
        dataResponse.setData(tagData);
        tagRepository.save(tag);
        return dataResponse;

    }

    public DataResponse<TagData> deleteTagById(long id) {
        DataResponse<TagData> dataResponse = new DataResponse<>();
        Tag tag = getTagById(id);

        if (tag == null){
            dataResponse.setError("invalid_request");
            dataResponse.setTimestamp(utilsService.getTimestampFromLocalDateTime(LocalDateTime.now()));
            return dataResponse;
        }
        TagData tagData = new TagData()
                .setId(tag.getId())
                .setTag(tag.getTag());
        tagRepository.delete(tag);

        dataResponse.setError("ok");
        dataResponse.setTimestamp(utilsService.getTimestampFromLocalDateTime(LocalDateTime.now()));
        dataResponse.setData(tagData);

        return dataResponse;
    }

    private Tag getTagById(long id){
        return tagRepository.findById(id).orElse(null);
    }

    private ListDataResponse<TagData> getTagResponse(Page<Tag> tags, int offset, int itemPerPage){
        ListDataResponse<TagData> listDataResponse = new ListDataResponse<>();
        listDataResponse.setError("ok");
        listDataResponse.setTimestamp(utilsService.getTimestampFromLocalDateTime(LocalDateTime.now()));
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

    public Set<Tag> addTagsToPost(String[] tagsString) {
        Set<Tag> tags = new HashSet<>();
        for (String tagFromNewPost : tagsString
        ) {
            Tag tag = tagRepository.findByName(tagFromNewPost);
            if (tag != null) {
                tags.add(tag);
            } else {
                Tag newTag = new Tag();
                newTag.setTag(tagFromNewPost);
                tagRepository.save(newTag);
                tags.add(newTag);
            }
        }

        return tags;
    }

}
