package com.skillbox.javapro21.service.serviceImpl;

import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.domain.Post;
import com.skillbox.javapro21.repository.PersonRepository;
import com.skillbox.javapro21.repository.PostRepository;
import com.skillbox.javapro21.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class PostServiceImpl extends AbstractMethodClass implements PostService {
    private final PersonRepository personRepository;
    private final PostRepository postRepository;

    @Autowired
    protected PostServiceImpl(PersonRepository personRepository, PostRepository postRepository) {
        super(personRepository);
        this.personRepository = personRepository;
        this.postRepository = postRepository;
    }

    public ListDataResponse<?> getPosts(String text, long dateFrom, long dateTo, int offset, int itemPerPage, String author, String tag, Principal principal) {
        Person person = findPersonByEmail(principal.getName());
        LocalDateTime datetimeFrom = (dateFrom == -1) ? LocalDateTime.from(Instant.now()) : LocalDateTime.from(Instant.ofEpochMilli(dateFrom));
        LocalDateTime datetimeTo = (dateTo == -1) ? LocalDateTime.from(Instant.now()) : LocalDateTime.from(Instant.ofEpochMilli(dateTo));
        List<Integer> blockers = personRepository.findBlockersId(person.getId());
        Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
        if (tag.equals("")) {

        }

//        Page<Post> pageablePostList = postRepository.findPostsByTextContainingByDateExcludingBlockers(text, datetimeFrom, datetimeTo, author, tag, pageable);
//        return getPostsResponse(offset, itemPerPage, pageablePostList, principal);
        return null;
    }

    private ListDataResponse<?> getPostsResponse(int offset, int itemPerPage, Page<Post> pageablePostList, Principal principal) {
        return null;
//                (ListDataResponse<Post>) new ListDataResponse<>()
//                .setPerPage(itemPerPage)
//                .setTimestamp(LocalDateTime.now())
//                .setOffset(offset)
//                .setTotal((int) pageablePostList.getTotalElements())
//                .setData(getPostForResponse(pageablePostList.toList()));
    }

    private List<Post> getPostForResponse(List<Post> listPosts) {
        List<Post> posts = new ArrayList<>();
        listPosts.forEach(post -> {
            Post somePost = getPost(post);
            posts.add(somePost);
        });
        return posts;
    }

    //todo: дописать добавление комментариев, тэгов и лайков как будут готовы
    private Post getPost(Post posts) {
        Post post = new Post()
                .setPostText(posts.getPostText())
                .setAuthor(posts.getAuthor())
                .setComments(null)
                .setId(posts.getId())
                .setLikes(null)
                .setTime(posts.getTime())
                .setTitle(posts.getTitle())
                .setIsBlocked(posts.getIsBlocked())
                .setTags(null);
        return post;
    }
}
