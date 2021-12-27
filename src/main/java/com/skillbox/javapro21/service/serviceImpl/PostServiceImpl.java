package com.skillbox.javapro21.service.serviceImpl;

import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.post.PostData;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.domain.Post;
import com.skillbox.javapro21.domain.PostLike;
import com.skillbox.javapro21.domain.Tag;
import com.skillbox.javapro21.repository.PersonRepository;
import com.skillbox.javapro21.repository.PostLikeRepository;
import com.skillbox.javapro21.repository.PostRepository;
import com.skillbox.javapro21.repository.TagRepository;
import com.skillbox.javapro21.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PostServiceImpl extends AbstractMethodClass implements PostService {
    private final PersonRepository personRepository;
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final PostLikeRepository postLikeRepository;

    @Autowired
    protected PostServiceImpl(PersonRepository personRepository, PostRepository postRepository, TagRepository tagRepository, PostLikeRepository postLikeRepository) {
        super(personRepository);
        this.personRepository = personRepository;
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
        this.postLikeRepository = postLikeRepository;
    }

    public ListDataResponse<PostData> getPosts(String text, long dateFrom, long dateTo, int offset, int itemPerPage, String author, String tag, Principal principal) {
        Person person = findPersonByEmail(principal.getName());

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        log.info(String.valueOf(dateFrom));
        String datetimeFromInstant = (dateFrom == -1) ? Arrays.stream(Instant.ofEpochMilli(dateFrom/1000).toString().split("T")).toList().get(0)
                : Arrays.stream(ZonedDateTime.now().minusYears(1).toInstant().toString().split("T")).toList().get(0);
        log.info(datetimeFromInstant);
        String datetimeToInstant = (dateTo == -1) ? Arrays.stream(Instant.ofEpochMilli(dateTo/1000).toString().split("T")).toList().get(0)
                : Arrays.stream(Instant.now().toString().split("T")).toList().get(0);
        LocalDateTime datetimeFrom = LocalDateTime.from(LocalDate.parse(datetimeFromInstant, dtf).atStartOfDay(ZoneId.systemDefault()));
        LocalDateTime datetimeTo = LocalDateTime.from(LocalDate.parse(datetimeToInstant, dtf).atStartOfDay(ZoneId.systemDefault()));
        log.info(String.valueOf(datetimeFrom));

        List<Long> blockers = personRepository.findBlockersId(person.getId());
        Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
        Page<Post> pageablePostList;
        if (tag.equals("")) {
            pageablePostList = postRepository.findPostsByTextContainingByDateExcludingBlockersWithoutTags(text, datetimeFrom, datetimeTo, author, blockers, pageable);
        } else {
            List<Long> tags = Arrays.stream(tag.split(";"))
                    .map(t -> tagRepository.findByTag(t).orElse(null))
                    .filter(Objects::nonNull)
                    .map(Tag::getId)
                    .collect(Collectors.toList());
            pageablePostList = postRepository.findPostsByTextContainingByDateExcludingBlockers(text, datetimeFrom, datetimeTo, author, tags, pageable);
        }
        return getPostsResponse(offset, itemPerPage, pageablePostList);
    }

    private ListDataResponse<PostData> getPostsResponse(int offset, int itemPerPage, Page<Post> pageablePostList) {
        ListDataResponse<PostData> contentListDataResponse = new ListDataResponse<>();
        contentListDataResponse.setPerPage(itemPerPage);
        contentListDataResponse.setTimestamp(LocalDateTime.now());
        contentListDataResponse.setOffset(offset);
        contentListDataResponse.setTotal((int) pageablePostList.getTotalElements());
        contentListDataResponse.setData(getPostForResponse(pageablePostList.toList()));
        return contentListDataResponse;
    }

    private List<PostData> getPostForResponse(List<Post> listPosts) {
        List<PostData> postsDataList = new ArrayList<>();
        listPosts.forEach(post -> {
            PostData postData = getPostData(post);
            postsDataList.add(postData);
        });
        return postsDataList;
    }

    //todo: дописать добавление комментариев, как будут готовы
    private PostData getPostData(Post posts) {
        Set<PostLike> likes = postLikeRepository.findPostLikeByPostId(posts.getId());
        List<String> collect = null;
        if (posts.getTags() != null) collect = posts.getTags().stream().map(Tag::getTag).toList();
        return new PostData()
                .setId(posts.getId())
                .setTime(posts.getTime())
                .setAuthor(getAuthData(posts.getAuthor(), null))
                .setTitle(posts.getTitle())
                .setPostText(posts.getPostText())
                .setBlocked(posts.getIsBlocked() != 0)
                .setLikes(likes.size())
                .setComments(null)
                .setTags(collect);
    }
}