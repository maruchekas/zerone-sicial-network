package com.skillbox.javapro21.service.impl;

import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.tag.TagData;
import com.skillbox.javapro21.domain.Tag;
import com.skillbox.javapro21.repository.TagRepository;
import com.skillbox.javapro21.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;
    private final UtilsService utilsService;

    @Override
    @Cacheable(value = "tags", key = "#tag + #offset + #itemPerPage")
    public ListDataResponse<TagData> getTags(String tag, int offset, int itemPerPage) {
        log.info("caching tags " + tag + " " + offset + " " + itemPerPage);
        Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
        Page<Tag> pageableTagList = tagRepository.findAllByTag(tag.toLowerCase(), pageable);
        return getTagResponse(pageableTagList, offset, itemPerPage);
    }

    @Override
    @CacheEvict(value = "tags", allEntries = true)
    public DataResponse<TagData> addTag(String tag) {
        DataResponse<TagData> dataResponse = new DataResponse<>();
        TagData tagData = new TagData();
        Tag tagByName = tagRepository.findByName(tag);
        if (tagByName == null) {
            tagByName = new Tag();
            tagByName.setTag(tag);
            dataResponse.setError("ok");
            dataResponse.setTimestamp(utilsService.getTimestampFromLocalDateTime(LocalDateTime.now()));
            dataResponse.setData(tagData);
            tagRepository.save(tagByName);
            tagData.setId(tagByName.getId());
            tagData.setTag(tag);
            return dataResponse;
        }
        dataResponse.setError("ok");
        dataResponse.setTimestamp(utilsService.getTimestampFromLocalDateTime(LocalDateTime.now()));
        tagData.setTag(tagByName.getTag());
        tagData.setId(tagByName.getId());
        dataResponse.setData(tagData);
        tagRepository.save(tagByName);
        return dataResponse;
    }

    @Override
    @CacheEvict(value = "tags", allEntries = true)
    public DataResponse<TagData> deleteTagById(long id) {
        DataResponse<TagData> dataResponse = new DataResponse<>();
        Tag tag = getTagById(id);
        if (tag == null) {
            return dataResponse
                    .setError("invalid_request")
                    .setTimestamp(utilsService.getTimestampFromLocalDateTime(LocalDateTime.now()));
        }
        TagData tagData = new TagData()
                .setId(tag.getId())
                .setTag(tag.getTag());
        tagRepository.delete(tag);
        return dataResponse
                .setError("ok")
                .setTimestamp(utilsService.getTimestampFromLocalDateTime(LocalDateTime.now()))
                .setData(tagData);
    }

    private Tag getTagById(long id) {
        return tagRepository.findById(id).orElse(null);
    }

    private ListDataResponse<TagData> getTagResponse(Page<Tag> tags, int offset, int itemPerPage) {
        ListDataResponse<TagData> listDataResponse = new ListDataResponse<>();
        listDataResponse.setError("ok");
        listDataResponse.setTimestamp(utilsService.getTimestampFromLocalDateTime(LocalDateTime.now()));
        listDataResponse.setTotal((int) tags.getTotalElements());
        listDataResponse.setOffset(offset);
        listDataResponse.setPerPage(itemPerPage);
        listDataResponse.setData(getTagDataList(tags.toList()));

        return listDataResponse;
    }

    private List<TagData> getTagDataList(List<Tag> tags) {
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
            Tag tag = tagRepository.findByTag(tagFromNewPost).orElseGet(() -> createNewTag(tagFromNewPost));
            tags.add(tag);
        }
        return tags;
    }

    private Tag createNewTag(String tagName) {
        Tag newTag = new Tag().setTag(tagName);
        tagRepository.save(newTag);
        return newTag;
    }

}
