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
import com.skillbox.javapro21.domain.enumeration.NotificationType;
import com.skillbox.javapro21.exception.*;
import com.skillbox.javapro21.repository.*;
import com.skillbox.javapro21.service.PostService;
import com.skillbox.javapro21.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.Principal;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

import static com.skillbox.javapro21.domain.enumeration.NotificationType.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    @Value("${mailjet.mail.email_admin}")
    private String adminEmail;

    private final UtilsService utilsService;
    private final TagService tagService;
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostCommentRepository postCommentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final MailjetSender mailjetSender;
    private final JdbcTemplate jdbcTemplate;
    private final NotificationRepository notificationRepository;

    @Override
    public ListDataResponse<PostData> getPosts(String text, long dateFrom, long dateTo, int offset, int itemPerPage,
                                               String author, String[] tags, Principal principal) {
        Person currentPerson = utilsService.findPersonByEmail(principal.getName());
        LocalDateTime datetimeFrom = (dateFrom != -1) ? utilsService.getLocalDateTime(dateFrom) : LocalDateTime.now().minusYears(1);
        LocalDateTime datetimeTo = (dateTo != -1) ? utilsService.getLocalDateTime(dateTo) : LocalDateTime.now();
        Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
        Page<Post> pageablePostList;
        if ((text.matches("\\s*") || text.equals("")) && tags.length == 0 && author.equals("")) {
            pageablePostList = postRepository.findAllPosts(datetimeFrom, datetimeTo, pageable);
        } else if (!text.isEmpty() && !text.matches("\\s*") && tags.length == 0 && author.equals("")) {
            pageablePostList = postRepository.findAllPostsByText(text.toLowerCase(Locale.ROOT), datetimeFrom, datetimeTo, pageable);
            if (pageablePostList.getTotalElements() == 0){
                text = utilsService.convertKbLayer(text);
                pageablePostList = postRepository.findAllPostsByText(text.toLowerCase(Locale.ROOT), datetimeFrom, datetimeTo, pageable);
            }
        } else if (!text.trim().isEmpty() && tags.length == 0 && !author.isEmpty()) {
            pageablePostList = postRepository.findPostsByTextByAuthorWithoutTagsContainingByDateExcludingBlockers(text.toLowerCase(Locale.ROOT), datetimeFrom, datetimeTo, author.toLowerCase(Locale.ROOT), pageable);
            if (pageablePostList.getTotalElements() == 0){
                text = utilsService.convertKbLayer(text);
                pageablePostList = postRepository.findPostsByTextByAuthorWithoutTagsContainingByDateExcludingBlockers(text.toLowerCase(Locale.ROOT), datetimeFrom, datetimeTo, author.toLowerCase(Locale.ROOT), pageable);
            }
        } else if ((text.matches("\\s*") || text.equals("")) && tags.length == 0 && !author.isEmpty()) {
            pageablePostList = postRepository.findAllPostsByAuthor(author.toLowerCase(Locale.ROOT), datetimeFrom, datetimeTo, pageable);
        } else {
            List<Long> tagsId = getTags(tags);
            pageablePostList = postRepository.findPostsByTextByAuthorByTagsContainingByDateExcludingBlockers(text.toLowerCase(Locale.ROOT), datetimeFrom, datetimeTo, author.toLowerCase(Locale.ROOT), tags, pageable);
        }
        return getPostsResponse(offset, itemPerPage, pageablePostList, currentPerson);
    }

    @Override
    public DataResponse<PostData> getPostById(Long id, Principal principal) throws PostNotFoundException {
        Person currentPerson = utilsService.findPersonByEmail(principal.getName());
        PostData postData = getPostData(postRepository.findPostById(id).orElseThrow(() -> new PostNotFoundException("Поста с таким айди не существует или пост заблокирован модератором")), currentPerson);
        return getDataResponse(postData);
    }

    @Override
    public DataResponse<PostData> putPostByIdAndMessageInDay(Long id, long publishDate, PostRequest postRequest, Principal principal) throws PostNotFoundException, AuthorAndUserEqualsException {
        Person person = utilsService.findPersonByEmail(principal.getName());
        Post post = postRepository.findPostById(id).orElseThrow(() -> new PostNotFoundException("Поста с таким айди не существует или пост заблокирован модератором"));
        if (!person.getId().equals(post.getAuthor().getId()))
            throw new AuthorAndUserEqualsException("Пользователь не может менять данные в этом посте");
        Set<Tag> tags = tagService.addTagsToPost(postRequest.getTags());
        post.setTitle(postRequest.getTitle())
                .setPostText(postRequest.getPostText())
                .setTags(tags)
                .setTime((publishDate == -1) ? LocalDateTime.now(ZoneOffset.UTC) : utilsService.getLocalDateTime(publishDate));
        post = postRepository.saveAndFlush(post);
        return getDataResponse(getPostData(post, person));
    }

    @Override
    public DataResponse<PostDeleteResponse> deletePostById(Long id, Principal principal) throws PostNotFoundException, AuthorAndUserEqualsException {
        Person person = utilsService.findPersonByEmail(principal.getName());
        Post post = postRepository.findPostById(id).orElseThrow(() -> new PostNotFoundException("Поста с таким айди не существует или пост заблокирован модератором"));
        if (!person.getId().equals(post.getAuthor().getId()))
            throw new AuthorAndUserEqualsException("Пользователь не может удалить этот пост");
        post.setIsBlocked(3);
        postRepository.save(post);
        return new DataResponse<PostDeleteResponse>()
                .setError("")
                .setTimestamp(utilsService.getTimestamp())
                .setData(new PostDeleteResponse()
                        .setId(post.getId()));
    }

    @Override
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
        return getDataResponse(getPostData(post, person));
    }

    @Override
    public ListDataResponse<CommentsData> getComments(Long id, int offset, int itemPerPage, Principal principal) throws PostNotFoundException {
        Person person = utilsService.findPersonByEmail(principal.getName());
        Post post = postRepository.findPostById(id).orElseThrow(() -> new PostNotFoundException("Поста с данным айди нет"));
        Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
        return getListDataResponseWithComments(pageable, post, person);
    }

    @Transactional
    @Override
    public DataResponse<CommentsData> postComments(Long id, CommentRequest commentRequest, Principal principal) throws PostNotFoundException, CommentNotFoundException {
        Person person = utilsService.findPersonByEmail(principal.getName());
        Post post = postRepository.findPostById(id).orElseThrow(() -> new PostNotFoundException("Поста с id " + id + "не существует"));
        PostComment postComment = new PostComment();
        if (commentRequest.getParentId() != null) {
            PostComment parentPostComment = postCommentRepository.findById(commentRequest.getParentId())
                    .orElseThrow(() -> new CommentNotFoundException("Комментария с данным id не существует"));
            postComment.setParent(parentPostComment);
        }
        postComment
                .setCommentText(commentRequest.getCommentText())
                .setIsBlocked(0)
                .setPerson(person)
                .setPost(post)
                .setTime(LocalDateTime.now(ZoneOffset.UTC));
        postCommentRepository.save(postComment);

        notificationRepository.save(new Notification()
                        .setSentTime(utilsService.getLocalDateTimeZoneOffsetUtc())
                        .setNotificationType(postComment.getParent() == null ? POST_COMMENT : COMMENT_COMMENT)
                        .setPerson(post.getAuthor())
                        .setEntityId(postComment.getParent() == null ? post.getId() : postComment.getParent().getId())
                        .setContact("contact"));

        return getCommentResponse(postComment, person);
    }

    @Override
    public DataResponse<CommentsData> putComments(Long id, Long commentId, CommentRequest commentRequest, Principal principal) throws PostNotFoundException, CommentNotFoundException, CommentNotAuthorException {
        Person person = utilsService.findPersonByEmail(principal.getName());
        PostComment postComment;
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
                .setTime(LocalDateTime.now(ZoneOffset.UTC));
        postCommentRepository.save(postComment);
        return getCommentResponse(postComment, person);
    }

    @Override
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
                .setTimestamp(utilsService.getTimestamp())
                .setData(new CommentDelete()
                        .setId(postComment.getId()));
    }

    @Override
    public DataResponse<CommentsData> recoverComments(Long id, Long commentId, Principal principal) throws CommentNotFoundException, CommentNotAuthorException {
        Person person = utilsService.findPersonByEmail(principal.getName());
        PostComment postComment = postCommentRepository.findPostCommentByIdAndParentIdWhichIsDelete(commentId, id)
                .orElseThrow(() -> new CommentNotFoundException("Подходящий комментарий не найден"));
        if (postComment.getPerson().getId().equals(person.getId())) {
            postComment.setIsBlocked(0);
            postCommentRepository.save(postComment);
        } else throw new CommentNotAuthorException("Восстановление пользователю " + person.getEmail() + "недоступно");
        return getCommentResponse(postComment, person);
    }

    @Override
    public DataResponse<MessageOkContent> ratPostController(Long id, Principal principal) throws PostNotFoundException, MailjetException, IOException {
        Post post = postRepository.findPostById(id).orElseThrow(() -> new PostNotFoundException("Пост не существует"));
        String message = "Жалоба от " + principal.getName() + " на пост с id: " + post.getId() + ", \n с названием: " + post.getTitle() + "\n и текстом " + post.getPostText();
        sendMessageForAdministration(message);
        return utilsService.getMessageOkResponse();
    }

    @Override
    public DataResponse<MessageOkContent> ratCommentController(Long id, Long commentId, Principal principal) throws CommentNotFoundException, MailjetException, IOException {
        PostComment postComment = postCommentRepository.findPostCommentByIdAndPostId(commentId, id)
                .orElseThrow(() -> new CommentNotFoundException("Комментария с данным parent_id не существует"));
        String message = "Жалоба от " + principal.getName() + " на комментарий с id: " + postComment.getId() + "\n и текстом " + postComment.getCommentText();
        sendMessageForAdministration(message);
        return utilsService.getMessageOkResponse();
    }

    @Override
    public ListDataResponse<PostData> getFeeds(String text, int offset, int itemPerPage, Principal principal) {
        Person currentPerson = utilsService.findPersonByEmail(principal.getName());
        Pageable pageable = PageRequest.of(offset, itemPerPage);

        String query =
                "(" +
                        "SELECT p.id FROM posts p " +
                        "JOIN persons ps ON ps.id = p.author_id " +
                        "WHERE ps.id IN (" +
                        "SELECT p.id FROM persons p " +
                        "JOIN friendship f on f.dst_person_id = p.id " +
                        "JOIN friendship_statuses fst on fst.id = f.status_id " +
                        "WHERE f.src_person_id = (?) " +
                        "AND (fst.name = 'FRIEND' OR fst.name = 'SUBSCRIBED')" +
                        ") " +
                        "AND p.is_Blocked = 0 " +
                        "AND ps.is_Blocked = 0 " +
                        "AND (p.title ILIKE CONCAT('%', (?), '%') OR p.post_text ILIKE CONCAT('%', (?),'%')) " +
                        "ORDER BY p.time DESC" +
                        ") " +
                        "UNION ALL " +
                        "(" +
                        "SELECT p.id FROM posts p " +
                        "JOIN persons ps ON ps.id = p.author_id " +
                        "LEFT JOIN post_likes pl ON pl.post_id = p.id " +
                        "WHERE p.author_id NOT IN (" +
                        "(" +
                        "SELECT p.id FROM persons p " +
                        "JOIN friendship f ON f.dst_person_id = p.id " +
                        "JOIN friendship_statuses fst ON fst.id = f.status_id " +
                        "WHERE f.src_person_id = (?) " +
                        "AND (fst.name = 'FRIEND' OR fst.name = 'SUBSCRIBED')" +
                        ") " +
                        "UNION ALL " +
                        "(" +
                        "SELECT p.id FROM persons p " +
                        "JOIN friendship f ON f.dst_person_id = p.id " +
                        "JOIN friendship_statuses fs ON fs.id = f.status_id " +
                        "WHERE f.src_person_id = (?) " +
                        "AND (fs.name = 'BLOCKED' OR fs.name = 'INTERLOCKED') " +
                        "OR (p.is_blocked != 0) " +
                        "GROUP BY p.id" +
                        ") " +
                        ") " +
                        "AND p.id != (?) " +
                        "AND p.is_Blocked = 0 " +
                        "AND ps.is_Blocked = 0 " +
                        "AND (p.title ILIKE CONCAT('%', (?),'%') OR p.post_text ILIKE CONCAT('%', (?),'%')) " +
                        "GROUP BY p.id " +
                        "ORDER BY count(pl) DESC, p.time DESC" +
                        ")";
        List<Long> ids = jdbcTemplate.query(
                query,
                (ResultSet rs, int rowNum) -> rs.getLong("id"),
                currentPerson.getId(), text, text,
                currentPerson.getId(), currentPerson.getId(), currentPerson.getId(), text, text);
        Page<Post> result = postRepository.findAllByIdIn(ids, pageable);
        return getPostsResponse(offset, itemPerPage, result, currentPerson);
    }

    private void sendMessageForAdministration(String message) throws MailjetException, IOException {
        mailjetSender.send(adminEmail, message);
    }

    private DataResponse<CommentsData> getCommentResponse(PostComment postComment, Person currentPerson) {
        return new DataResponse<CommentsData>().setError("")
                .setTimestamp(utilsService.getTimestamp())
                .setData(getCommentsData(postComment, currentPerson));
    }

    private ListDataResponse<CommentsData> getListDataResponseWithComments(Pageable pageable, Post post, Person currentPerson) {
        Page<PostComment> pageablePostComments = postCommentRepository.findPostCommentsByPostId(post.getId(), pageable);
        return getPostCommentResponse(pageablePostComments, pageable, currentPerson);
    }

    private ListDataResponse<CommentsData> getPostCommentResponse(Page<PostComment> pageablePostComments, Pageable pageable, Person currentPerson) {
        ListDataResponse<CommentsData> commentsDataListDataResponse = new ListDataResponse<>();
        commentsDataListDataResponse.setPerPage(pageable.getPageSize())
                .setTimestamp(utilsService.getTimestamp())
                .setOffset((int) pageable.getOffset())
                .setTotal(pageablePostComments.getTotalPages())
                .setData(getCommentDataForResponse(pageablePostComments.toList(), currentPerson));
        return commentsDataListDataResponse;
    }

    private List<CommentsData> getCommentDataForResponse(List<PostComment> comments, Person currentPerson) {
        List<CommentsData> commentsDataArrayList = new ArrayList<>();
        comments.forEach(postComment -> {
            CommentsData commentsData = getCommentsData(postComment, currentPerson);
            commentsDataArrayList.add(commentsData);
        });
        return commentsDataArrayList;
    }

    private CommentsData getCommentsData(PostComment postComment, Person currentPerson) {
        CommentsData commentsData = new CommentsData();
        List<PostComment> postCommentsByParentId = postCommentRepository.findPostCommentsByParentId(postComment.getId());
        List<CommentLike> likes = commentLikeRepository.findAllByCommentId(postComment.getId());
        commentsData
                .setCommentText(postComment.getCommentText())
                .setId(postComment.getId())
                .setPostId(postComment.getPost().getId())
                .setTime(postComment.getTime().toInstant(ZoneOffset.UTC).toEpochMilli())
                .setAuthor(utilsService.getAuthData(postComment.getPerson(), null))
                .setBlocked(postComment.getIsBlocked() == 0)
                .setMyLike(likes.stream().map(CommentLike::getPerson).toList().contains(currentPerson))
                .setLikes(likes.size())
                .setSubComments(getSubCommentsData(postCommentsByParentId, currentPerson));
        if (postComment.getParent() != null) commentsData.setParentId(postComment.getParent().getId());
        return commentsData;
    }

    private List<CommentsData> getSubCommentsData(List<PostComment> postCommentsByParentId, Person currentPerson) {
        List<CommentsData> commentsDataList = new ArrayList<>();
        postCommentsByParentId.forEach(postComment -> {
            CommentsData commentsData = getCommentsData(postComment, currentPerson);
            commentsDataList.add(commentsData);
        });
        return commentsDataList;
    }

    protected DataResponse<PostData> getDataResponse(PostData postData) {
        return new DataResponse<PostData>()
                .setError("")
                .setTimestamp(utilsService.getTimestamp())
                .setData(postData);
    }

    protected ListDataResponse<PostData> getPostsResponse(int offset, int itemPerPage, Page<Post> pageablePostList, Person currentPerson) {
        return new ListDataResponse<PostData>()
                .setPerPage(itemPerPage)
                .setTimestamp(utilsService.getTimestamp())
                .setOffset(offset)
                .setTotal((int) pageablePostList.getTotalElements())
                .setData(getPostForResponse(pageablePostList.toList(), currentPerson));
    }

    protected ListDataResponse<PostData> getPostsResponse(int offset, int itemPerPage, Page<Post> pageablePostList, Page<Post> bestPosts, Person currentPerson) {
        List<Post> postsForResponse = new ArrayList<>(pageablePostList.toList());
        for (Post p : bestPosts.toList()) {
            if (!postsForResponse.contains(p)) {
                postsForResponse.add(p);
            }
        }
        return new ListDataResponse<PostData>()
                .setOffset(offset)
                .setPerPage(itemPerPage)
                .setTimestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .setTotal(postsForResponse.size())
                .setData(getPostForResponse(postsForResponse, currentPerson));
    }

    private List<PostData> getPostForResponse(List<Post> listPosts, Person person) {
        List<PostData> postsDataList = new ArrayList<>();
        listPosts.forEach(post -> {
            PostData postData = null;
            try {
                postData = getPostData(post, person);
            } catch (PostNotFoundException e) {
                e.printStackTrace();
            }
            postsDataList.add(postData);
        });
        return postsDataList;
    }

    protected PostData getPostData(Post post, Person currentPerson) throws PostNotFoundException {
        List<PostLike> likes = postLikeRepository.findPostLikeByPostId(post.getId());
        List<String> collect = null;
        if (post.getTags() != null) collect = post.getTags().stream().map(Tag::getTag).toList();
        return new PostData()
                .setId(post.getId())
                .setAuthor(utilsService.getAuthData(post.getAuthor(), null))
                .setTime(post.getTime().toInstant(ZoneOffset.UTC).toEpochMilli())
                .setTitle(post.getTitle())
                .setPostText(post.getPostText())
                .setBlocked(post.getIsBlocked() != 0)
                .setMyLike(likes.stream().map(PostLike::getPerson).toList().contains(currentPerson))
                .setLikes(likes.size())
                .setComments(getCommentsDataResponseForPost(post.getId(), currentPerson))
                .setType(post.getTime().isBefore(LocalDateTime.now()) ? "POSTED" : "QUEUED")
                .setTags(collect);
    }

    private List<CommentsData> getCommentsDataResponseForPost(Long id, Person currentPerson) {
        List<PostComment> pageablePostComments = postCommentRepository.findPostCommentsByPostIdList(id);
        List<CommentsData> commentsDataArrayList = new ArrayList<>();
        pageablePostComments.forEach(postComment -> {
            CommentsData commentsData = getCommentsData(postComment, currentPerson);
            if (commentsData.getParentId() == null) {
                commentsDataArrayList.add(commentsData);
            }
        });
        return commentsDataArrayList;
    }

    private List<Long> getTags(String[] tags) {

        return Arrays.stream(tags)
                .map(t -> tagRepository.findByTag(t).orElse(null))
                .filter(Objects::nonNull)
                .map(Tag::getId)
                .collect(Collectors.toList());
    }
}