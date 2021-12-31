package com.skillbox.javapro21.service.impl;

import com.skillbox.javapro21.api.request.post.PostRequest;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.post.CommentsData;
import com.skillbox.javapro21.api.response.post.PostData;
import com.skillbox.javapro21.api.response.post.PostDeleteResponse;
import com.skillbox.javapro21.domain.*;
import com.skillbox.javapro21.exception.AuthorAndUserEqualsException;
import com.skillbox.javapro21.exception.PostNotFoundException;
import com.skillbox.javapro21.exception.PostRecoveryException;
import com.skillbox.javapro21.repository.PostCommentRepository;
import com.skillbox.javapro21.repository.PostLikeRepository;
import com.skillbox.javapro21.repository.PostRepository;
import com.skillbox.javapro21.repository.TagRepository;
import com.skillbox.javapro21.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final UtilsService utilsService;
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostCommentRepository postCommentRepository;

    public ListDataResponse<PostData> getPosts(String text, long dateFrom, long dateTo, int offset, int itemPerPage, String author, String tag, Principal principal) {
        LocalDateTime datetimeFrom = (dateFrom != -1) ? utilsService.getLocalDateTime(dateFrom) : LocalDateTime.now().minusYears(1);
        LocalDateTime datetimeTo = (dateTo != -1) ? utilsService.getLocalDateTime(dateTo) : LocalDateTime.now();
        Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
        Page<Post> pageablePostList;
        if (tag.equals("")) {
            pageablePostList = postRepository.findPostsByTextByAuthorWithoutTagsContainingByDateExcludingBlockers(text, datetimeFrom, datetimeTo, author, pageable);
        } else {
            List<Long> tags = getTags(tag);
            pageablePostList = postRepository.findPostsByTextByAuthorByTagsContainingByDateExcludingBlockers(text, datetimeFrom, datetimeTo, author, tags, pageable);
        }
        return getPostsResponse(offset, itemPerPage, pageablePostList);
    }

    public DataResponse<PostData> getPostsById(Long id, Principal principal) throws PostNotFoundException {
        PostData postData = getPostData(postRepository.findPostById(id).orElseThrow(() -> new PostNotFoundException("Поста с таким айди не существует или пост заблокирован модератором")));
        return getDataResponse(postData);
    }

    public DataResponse<PostData> putPostByIdAndMessageInDay(Long id, long publishDate, PostRequest postRequest, Principal principal) throws PostNotFoundException, AuthorAndUserEqualsException {
        Person person = utilsService.findPersonByEmail(principal.getName());
        Post post = postRepository.findPostById(id).orElseThrow(() -> new PostNotFoundException("Поста с таким айди не существует или пост заблокирован модератором"));
        if (!person.getId().equals(post.getAuthor().getId()))
            throw new AuthorAndUserEqualsException("Пользователь не может менять данные в этом посте");
        post.setTitle(postRequest.getTitle())
                .setPostText(postRequest.getPostText())
                .setTime((publishDate == -1) ? LocalDateTime.now() : utilsService.getLocalDateTime(publishDate));
        post = postRepository.saveAndFlush(post);
        return getDataResponse(getPostData(post));
    }

    public DataResponse<PostDeleteResponse> deletePostById(Long id, Principal principal) throws PostNotFoundException, AuthorAndUserEqualsException {
        Person person = utilsService.findPersonByEmail(principal.getName());
        Post post = postRepository.findPostById(id).orElseThrow(() -> new PostNotFoundException("Поста с таким айди не существует или пост заблокирован модератором"));
        if (!person.getId().equals(post.getAuthor().getId()))
            throw new AuthorAndUserEqualsException("Пользователь не может удалить этот пост");
        post.setIsBlocked(3);
        postRepository.save(post);
        return new DataResponse<PostDeleteResponse>()
                .setError("")
                .setTimestamp(LocalDateTime.now())
                .setData(new PostDeleteResponse()
                        .setId(post.getId()));
    }

    public DataResponse<PostData> recoverPostById(Long id, Principal principal) throws PostNotFoundException, AuthorAndUserEqualsException, PostRecoveryException {
        Person person = utilsService.findPersonByEmail(principal.getName());
        Post post = postRepository.findDeletedPostById(id).orElseThrow(() -> new PostNotFoundException("Поста с таким айди не существует или пост заблокирован модератором"));
        if (!person.getId().equals(post.getAuthor().getId()))
            throw new AuthorAndUserEqualsException("Пользователь не может восстановить этот пост");
        if (post.getIsBlocked() == 0)
            throw new PostRecoveryException("Данный пост не заблокирован");
        if (post.getIsBlocked() == 1)
            throw new PostRecoveryException("Данный пост заблокирован модератором");
        post.setIsBlocked(0);
        postRepository.save(post);
        return getDataResponse(getPostData(post));
    }

    public ListDataResponse<CommentsData> getComments(Long id, int offset, int itemPerPage, Principal principal) throws PostNotFoundException {
        Person person = utilsService.findPersonByEmail(principal.getName());
        Post post = postRepository.findPostById(id).orElseThrow(() -> new PostNotFoundException("Поста с данным айди нет"));
        Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
        return getListDataResponseWithComments(person, pageable, post);
    }

    private ListDataResponse<CommentsData> getListDataResponseWithComments(Person person, Pageable pageable, Post post) {
        Page<PostComment> pageablePostComments = postCommentRepository.findPostCommentsByPotId(post.getId(), pageable);
        return getPostCommentResponse(pageablePostComments, person, pageable);
    }

    private ListDataResponse<CommentsData> getPostCommentResponse(Page<PostComment> pageablePostComments, Person person, Pageable pageable) {
        return new ListDataResponse<CommentsData>()
                .setPerPage(pageable.getPageSize())
                .setTimestamp(LocalDateTime.now())
                .setOffset((int) pageable.getOffset())
                .setTotal(pageablePostComments.getTotalPages())
                .setData(getCommentDataForResponse(pageablePostComments.toList(), person));
    }

    private List<CommentsData> getCommentDataForResponse(List<PostComment> comments, Person person) {
        List<CommentsData> commentsDataArrayList = new ArrayList<>();
        comments.forEach(postComment -> {
//            CommentsData commentsData = getCommentsData(postComment, person);
//            postComment.getParentId()
        });
        return null;
    }

    protected DataResponse<PostData> getDataResponse(PostData postData) {
        return new DataResponse<PostData>()
                .setError("")
                .setTimestamp(LocalDateTime.now())
                .setData(postData);
    }

    protected ListDataResponse<PostData> getPostsResponse(int offset, int itemPerPage, Page<Post> pageablePostList) {
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
    protected PostData getPostData(Post posts) {
        Set<PostLike> likes = postLikeRepository.findPostLikeByPostId(posts.getId());
        List<String> collect = null;
        if (posts.getTags() != null) collect = posts.getTags().stream().map(Tag::getTag).toList();
        return new PostData()
                .setId(posts.getId())
                .setTime(posts.getTime())
                .setAuthor(utilsService.getAuthData(posts.getAuthor(), null))
                .setTitle(posts.getTitle())
                .setPostText(posts.getPostText())
                .setBlocked(posts.getIsBlocked() != 0)
                .setLikes(likes.size())
                .setComments(null)
                .setTags(collect);
    }

    private List<Long> getTags(String tag) {
        return Arrays.stream(tag.split(";"))
                .map(t -> tagRepository.findByTag(t).orElse(null))
                .filter(Objects::nonNull)
                .map(Tag::getId)
                .collect(Collectors.toList());
    }
}