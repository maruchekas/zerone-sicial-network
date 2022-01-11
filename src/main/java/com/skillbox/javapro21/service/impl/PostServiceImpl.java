package com.skillbox.javapro21.service.impl;

import com.mailjet.client.errors.MailjetException;
import com.skillbox.javapro21.api.request.post.CommentRequest;
import com.skillbox.javapro21.api.request.post.PostRequest;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.MessageOkContent;
import com.skillbox.javapro21.api.response.post.CommentDelete;
import com.skillbox.javapro21.api.response.post.CommentsData;
import com.skillbox.javapro21.api.response.post.PostData;
import com.skillbox.javapro21.api.response.post.PostDeleteResponse;
import com.skillbox.javapro21.config.MailjetSender;
import com.skillbox.javapro21.domain.*;
import com.skillbox.javapro21.exception.*;
import com.skillbox.javapro21.repository.*;
import com.skillbox.javapro21.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.Principal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    @Value("${mailjet.mail.email_admin}")
    private String adminEmail;

    private final UtilsService utilsService;
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostCommentRepository postCommentRepository;
    private final PersonRepository personRepository;
    private final MailjetSender mailjetSender;

    public ListDataResponse<PostData> getPosts(String text, long dateFrom, long dateTo, int offset, int itemPerPage, String author, String tag, Principal principal) {
        LocalDateTime datetimeFrom = (dateFrom != -1) ? utilsService.getLocalDateTime(dateFrom) : LocalDateTime.now().minusYears(1);
        LocalDateTime datetimeTo = (dateTo != -1) ? utilsService.getLocalDateTime(dateTo) : LocalDateTime.now();
        Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
        Page<Post> pageablePostList;
        if (text.equals("") && tag.equals("") && author.equals("")) {
            pageablePostList = postRepository.findAllPosts(datetimeFrom, datetimeTo, pageable);
        } else if (tag.equals("") && author.equals("")) {
            pageablePostList = postRepository.findAllPostsByText(text, datetimeFrom, datetimeTo, pageable);
        } else if (tag.equals("")) {
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
                .setTimestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
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

    public ListDataResponse<CommentsData> getComments(Long id, int offset, int itemPerPage) throws PostNotFoundException {
        Post post = postRepository.findPostById(id).orElseThrow(() -> new PostNotFoundException("Поста с данным айди нет"));
        Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
        return getListDataResponseWithComments(pageable, post);
    }

    public DataResponse<CommentsData> postComments(Long id, CommentRequest commentRequest, Principal principal) throws PostNotFoundException, CommentNotFoundException {
        Person person = utilsService.findPersonByEmail(principal.getName());
        Post post = postRepository.findPostById(id).orElseThrow(() -> new PostNotFoundException("Поста с id " + id + "не существует"));
        PostComment postComment = new PostComment();
        if (commentRequest.getParentId() != null) {
            PostComment parentPostComment = postCommentRepository.findById(commentRequest.getParentId())
                    .orElseThrow(() -> new CommentNotFoundException("Комментария с данным id не существует"));
            postComment.setParent(parentPostComment);
        }
        postComment.setCommentText(commentRequest.getCommentText())
                .setIsBlocked(0)
                .setPerson(person)
                .setPost(post)
                .setTime(LocalDateTime.now());
        postCommentRepository.save(postComment);
        return getCommentResponse(postComment);
    }

    public DataResponse<CommentsData> putComments(Long id, Long commentId, CommentRequest commentRequest, Principal principal) throws PostNotFoundException, CommentNotFoundException, CommentNotAuthorException {
        Person person = utilsService.findPersonByEmail(principal.getName());
        PostComment postComment = null;
        if (commentRequest.getParentId() != null) {
            postComment = postCommentRepository.findPostCommentByIdAndPostId(id, commentId)
                    .orElseThrow(() -> new CommentNotFoundException("Комментария с данным parent_id не существует"));
        } else
            postComment = postCommentRepository.findById(commentId)
                    .orElseThrow(() -> new CommentNotFoundException("Комментария с данным id не существует"));

        if (!person.getId().equals(postComment.getPerson().getId()))
            throw new CommentNotAuthorException("Пользователь не имеет прав редактировать данный комментарий");
        postComment
                .setCommentText(commentRequest.getCommentText())
                .setTime(LocalDateTime.now());
        postCommentRepository.save(postComment);
        return getCommentResponse(postComment);
    }

    public DataResponse<CommentDelete> deleteComments(Long id, Long commentId, Principal principal) throws CommentNotFoundException, CommentNotAuthorException {
        Person person = utilsService.findPersonByEmail(principal.getName());
        PostComment postComment = postCommentRepository.findPostCommentByIdAndPostId(commentId, id)
                .orElseThrow(() -> new CommentNotFoundException("Комментария с данным parent_id не существует"));
        if (postComment.getPerson().getId().equals(person.getId())) {
            postComment.setIsBlocked(2);
            postCommentRepository.save(postComment);
        } else throw new CommentNotAuthorException("Удаление пользователю " + person.getEmail() + "недоступно");
        return new DataResponse<CommentDelete>()
                .setError("")
                .setTimestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .setData(new CommentDelete()
                        .setId(postComment.getId()));
    }

    public DataResponse<CommentsData> recoverComments(Long id, Long commentId, Principal principal) throws CommentNotFoundException, CommentNotAuthorException {
        Person person = utilsService.findPersonByEmail(principal.getName());
        PostComment postComment = postCommentRepository.findPostCommentByIdAndParentIdWhichIsDelete(commentId, id)
                .orElseThrow(() -> new CommentNotFoundException("Подходящий комментарий не найден"));
        if (postComment.getPerson().getId().equals(person.getId())) {
            postComment.setIsBlocked(0);
            postCommentRepository.save(postComment);
        } else throw new CommentNotAuthorException("Восстановление пользователю " + person.getEmail() + "недоступно");
        return getCommentResponse(postComment);
    }

    public DataResponse<MessageOkContent> ratPostController(Long id, Principal principal) throws PostNotFoundException, MailjetException, IOException {
        Post post = postRepository.findPostById(id).orElseThrow(() -> new PostNotFoundException("Пост не существует"));
        String message = "Жалоба от " + principal.getName() + " на пост с id: " + post.getId() + ", \n с названием: " + post.getTitle() + "\n и текстом " + post.getPostText();
        sendMessageForAdministration(message);
        return utilsService.getMessageOkResponse();
    }

    public DataResponse<MessageOkContent> ratCommentController(Long id, Long commentId, Principal principal) throws CommentNotFoundException, MailjetException, IOException {
        PostComment postComment = postCommentRepository.findPostCommentByIdAndPostId(commentId, id)
                .orElseThrow(() -> new CommentNotFoundException("Комментария с данным parent_id не существует"));
        String message = "Жалоба от " + principal.getName() + " на комментарий с id: " + postComment.getId() + "\n и текстом " + postComment.getCommentText();
        sendMessageForAdministration(message);
        return utilsService.getMessageOkResponse();
    }

    public ListDataResponse<PostData> getFeeds(String text, int offset, int itemPerPage, Principal principal) {
        Person person = utilsService.findPersonByEmail(principal.getName());
        Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
        List<Long> friendsAndSubscribersIds = personRepository.findAllFriendsAndSubscribersByPersonId(person.getId());
        friendsAndSubscribersIds.add(person.getId());
        Page<Post> postPage = postRepository.findPostsByTextExcludingBlockers(text, friendsAndSubscribersIds, pageable);
        return getPostsResponse(offset, itemPerPage, postPage);
    }

    private void sendMessageForAdministration(String message) throws MailjetException, IOException {
        mailjetSender.send(adminEmail, message);
    }

    private DataResponse<CommentsData> getCommentResponse(PostComment postComment) {
        return new DataResponse<CommentsData>().setError("")
                .setTimestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .setData(getCommentsData(postComment));
    }

    private ListDataResponse<CommentsData> getListDataResponseWithComments(Pageable pageable, Post post) {
        Page<PostComment> pageablePostComments = postCommentRepository.findPostCommentsByPostId(post.getId(), pageable);
        return getPostCommentResponse(pageablePostComments, pageable);
    }

    private ListDataResponse<CommentsData> getPostCommentResponse(Page<PostComment> pageablePostComments, Pageable pageable) {
        ListDataResponse<CommentsData> commentsDataListDataResponse = new ListDataResponse<>();
        commentsDataListDataResponse.setPerPage(pageable.getPageSize())
                .setTimestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .setOffset((int) pageable.getOffset())
                .setTotal(pageablePostComments.getTotalPages())
                .setData(getCommentDataForResponse(pageablePostComments.toList()));
        return commentsDataListDataResponse;
    }

    private List<CommentsData> getCommentDataForResponse(List<PostComment> comments) {
        List<CommentsData> commentsDataArrayList = new ArrayList<>();
        comments.forEach(postComment -> {
            CommentsData commentsData = getCommentsData(postComment);
            commentsDataArrayList.add(commentsData);
        });
        return commentsDataArrayList;
    }

    private CommentsData getCommentsData(PostComment postComment) {
        CommentsData commentsData = new CommentsData();
        commentsData
                .setCommentText(postComment.getCommentText())
                .setId(postComment.getId())
                .setPostId(postComment.getPost().getId())
                .setTime(postComment.getTime().toInstant(ZoneOffset.UTC).toEpochMilli())
                .setAuthorId(postComment.getPerson().getId())
                .setBlocked(postComment.getIsBlocked() == 0);
        if (postComment.getParent() != null) commentsData.setParentId(postComment.getParent().getId());
        return commentsData;
    }

    protected DataResponse<PostData> getDataResponse(PostData postData) {
        return new DataResponse<PostData>()
                .setError("")
                .setTimestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .setData(postData);
    }

    protected ListDataResponse<PostData> getPostsResponse(int offset, int itemPerPage, Page<Post> pageablePostList) {
        ListDataResponse<PostData> contentListDataResponse = new ListDataResponse<>();
        contentListDataResponse.setPerPage(itemPerPage);
        contentListDataResponse.setTimestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());
        contentListDataResponse.setOffset(offset);
        contentListDataResponse.setTotal((int) pageablePostList.getTotalElements());
        contentListDataResponse.setData(getPostForResponse(pageablePostList.toList()));
        return contentListDataResponse;
    }

    private List<PostData> getPostForResponse(List<Post> listPosts) {
        List<PostData> postsDataList = new ArrayList<>();
        listPosts.forEach(post -> {
            PostData postData = null;
            try {
                postData = getPostData(post);
            } catch (PostNotFoundException e) {
                e.printStackTrace();
            }
            postsDataList.add(postData);
        });
        return postsDataList;
    }

    protected PostData getPostData(Post posts) throws PostNotFoundException {
        Set<PostLike> likes = postLikeRepository.findPostLikeByPostId(posts.getId());
        List<String> collect = null;
        if (posts.getTags() != null) collect = posts.getTags().stream().map(Tag::getTag).toList();
        return new PostData()
                .setId(posts.getId())
                .setAuthor(utilsService.getAuthData(posts.getAuthor(), null))
                .setTime(posts.getTime().toInstant(ZoneOffset.UTC).toEpochMilli())
                .setTitle(posts.getTitle())
                .setPostText(posts.getPostText())
                .setBlocked(posts.getIsBlocked() != 0)
                .setLikes(likes.size())
                .setComments(getCommentsDataResponseForPost(posts.getId()))
                .setType(posts.getTime().isBefore(LocalDateTime.now()) ? "POSTED" : "QUEUED")
                .setTags(collect);
    }

    private List<CommentsData> getCommentsDataResponseForPost(Long id) {
        List<PostComment> pageablePostComments = postCommentRepository.findPostCommentsByPostIdList(id);
        List<CommentsData> commentsDataArrayList = new ArrayList<>();
        pageablePostComments.forEach(postComment -> {
            CommentsData commentsData = getCommentsData(postComment);
            commentsDataArrayList.add(commentsData);
        });
        return commentsDataArrayList;
    }

    private List<Long> getTags(String tag) {
        return Arrays.stream(tag.split(";"))
                .map(t -> tagRepository.findByTag(t).orElse(null))
                .filter(Objects::nonNull)
                .map(Tag::getId)
                .collect(Collectors.toList());
    }
}