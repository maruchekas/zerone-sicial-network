package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.AbstractTest;
import com.skillbox.javapro21.api.request.like.LikeRequest;
import com.skillbox.javapro21.domain.*;
import com.skillbox.javapro21.domain.enumeration.MessagesPermission;
import com.skillbox.javapro21.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(value = {"classpath:application-test.properties"})
class LikeControllerTest extends AbstractTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostCommentRepository postCommentRepository;
    @Autowired
    private PostLikeRepository postLikeRepository;
    @Autowired
    private CommentLikeRepository commentLikeRepository;

    Person person1, person2, person3;
    Post post1, post3;
    PostComment comment1, comment5;
    PostLike postLike1;
    CommentLike commentLike1;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    @BeforeEach
    public void setup() {
        super.setup();
        String verifyEmail = "@test.ru";
        String password = "123456";
        String firstName = "Arcadiy";
        String lastName = "Parovozov";
        LocalDateTime reg_date = LocalDateTime.now();
        String conf_code = "123";

        person1 = new Person()
                .setEmail("person1" + verifyEmail)
                .setPassword(passwordEncoder.encode(password))
                .setFirstName(firstName)
                .setLastName(lastName)
                .setConfirmationCode("123")
                .setRegDate(reg_date)
                .setConfirmationCode(conf_code)
                .setMessagesPermission(MessagesPermission.ALL)
                .setIsBlocked(0)
                .setIsApproved(1)
                .setLastOnlineTime(LocalDateTime.now());
        person2 = new Person()
                .setEmail("person2" + verifyEmail)
                .setPassword(passwordEncoder.encode(password))
                .setFirstName(firstName + "-Альфа")
                .setLastName(lastName)
                .setConfirmationCode("123")
                .setRegDate(reg_date)
                .setConfirmationCode(conf_code)
                .setMessagesPermission(MessagesPermission.ALL)
                .setIsBlocked(0)
                .setIsApproved(1)
                .setLastOnlineTime(LocalDateTime.now());
        person3 = new Person()
                .setEmail("person3" + verifyEmail)
                .setPassword(passwordEncoder.encode(password))
                .setFirstName(firstName + "-Бетта")
                .setLastName(lastName)
                .setConfirmationCode("123")
                .setRegDate(reg_date)
                .setConfirmationCode(conf_code)
                .setMessagesPermission(MessagesPermission.ALL)
                .setIsBlocked(0)
                .setIsApproved(1)
                .setLastOnlineTime(LocalDateTime.now());

        personRepository.save(person1);
        personRepository.save(person2);
        personRepository.save(person3);

        post1 = new Post()
                .setTime(LocalDateTime.now().minusDays(1))
                .setAuthor(person2)
                .setTitle("Моржи")
                .setPostText("Лучше моржей только тюлени")
                .setIsBlocked(0)
                .setLikes(null)
                .setComments(null);
        Post post2 = new Post()
                .setTime(LocalDateTime.now().minusDays(1))
                .setAuthor(person2)
                .setTitle("Тюлени и моржи")
                .setPostText("I think about animals every day....")
                .setIsBlocked(0)
                .setLikes(null)
                .setComments(null);
        post3 = new Post()
                .setTime(LocalDateTime.now().minusDays(1))
                .setAuthor(person1)
                .setTitle("Тюлени и моржи")
                .setPostText("I think about animals every day....")
                .setIsBlocked(0)
                .setLikes(null)
                .setComments(null);

        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);

        postLike1 = new PostLike()
                .setTime(LocalDateTime.now())
                .setPost(post1)
                .setPerson(person1);
        PostLike postLike2 = new PostLike()
                .setTime(LocalDateTime.now())
                .setPost(post2)
                .setPerson(person1);
        PostLike postLike3 = new PostLike()
                .setTime(LocalDateTime.now())
                .setPost(post1)
                .setPerson(person2);
        PostLike postLike4 = new PostLike()
                .setTime(LocalDateTime.now())
                .setPost(post2)
                .setPerson(person2);

        postLikeRepository.save(postLike1);
        postLikeRepository.save(postLike2);
        postLikeRepository.save(postLike3);
        postLikeRepository.save(postLike4);

        comment1 = new PostComment()
                .setTime(LocalDateTime.now())
                .setCommentText("Коммент1")
                .setPost(post1)
                .setPerson(person1);
        PostComment comment2 = new PostComment()
                .setTime(LocalDateTime.now())
                .setCommentText("Коммент2")
                .setPost(post1)
                .setPerson(person2);
        PostComment comment3 = new PostComment()
                .setTime(LocalDateTime.now())
                .setCommentText("Коммент3")
                .setPost(post2)
                .setPerson(person1);
        PostComment comment4 = new PostComment()
                .setTime(LocalDateTime.now())
                .setCommentText("Коммент4")
                .setPost(post2)
                .setPerson(person2);

        comment5 = new PostComment()
                .setTime(LocalDateTime.now())
                .setCommentText("Коммент5")
                .setPost(post2)
                .setPerson(person2);

        postCommentRepository.save(comment1);
        postCommentRepository.save(comment2);
        postCommentRepository.save(comment3);
        postCommentRepository.save(comment4);
        postCommentRepository.save(comment5);

        commentLike1 = new CommentLike()
                .setTime(LocalDateTime.now())
                .setComment(comment1)
                .setPerson(person1);
        CommentLike commentLike2 = new CommentLike()
                .setTime(LocalDateTime.now())
                .setComment(comment2)
                .setPerson(person1);
        CommentLike commentLike3 = new CommentLike()
                .setTime(LocalDateTime.now())
                .setComment(comment3)
                .setPerson(person1);
        CommentLike commentLike4 = new CommentLike()
                .setTime(LocalDateTime.now())
                .setComment(comment4)
                .setPerson(person1);
        CommentLike commentLike5 = new CommentLike()
                .setTime(LocalDateTime.now())
                .setComment(comment1)
                .setPerson(person2);
        CommentLike commentLike6 = new CommentLike()
                .setTime(LocalDateTime.now())
                .setComment(comment2)
                .setPerson(person2);
        CommentLike commentLike7 = new CommentLike()
                .setTime(LocalDateTime.now())
                .setComment(comment3)
                .setPerson(person2);
        CommentLike commentLike8 = new CommentLike()
                .setTime(LocalDateTime.now())
                .setComment(comment4)
                .setPerson(person2);

        commentLikeRepository.save(commentLike1);
        commentLikeRepository.save(commentLike2);
        commentLikeRepository.save(commentLike3);
        commentLikeRepository.save(commentLike4);
        commentLikeRepository.save(commentLike5);
        commentLikeRepository.save(commentLike6);
        commentLikeRepository.save(commentLike7);
        commentLikeRepository.save(commentLike8);

    }

    @AfterEach
    public void cleanup() {
        personRepository.deleteAll();
        postRepository.deleteAll();
        postCommentRepository.deleteAll();
        postLikeRepository.deleteAll();
        commentLikeRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "person1@test.ru", authorities = "user:write")
    void isLiked() throws Exception {
        LikeRequest succsPostRequest = new LikeRequest();
        succsPostRequest.setType("Post");
        succsPostRequest.setItemId(post1.getId());
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/liked")
                        .principal(() -> "person1@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(succsPostRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.likes").value(true));

        LikeRequest succsCommentRequest = new LikeRequest();
        succsCommentRequest.setType("Comment");
        succsCommentRequest.setItemId(comment1.getId());
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/liked")
                        .principal(() -> "person1@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(succsCommentRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.likes").value(true));

        LikeRequest failPostRequest = new LikeRequest();
        failPostRequest.setType("Post");
        failPostRequest.setItemId(post3.getId());
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/liked")
                        .principal(() -> "person1@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(failPostRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.likes").value(false));

        LikeRequest failCommentRequest = new LikeRequest();
        failCommentRequest.setType("Comment");
        failCommentRequest.setItemId(comment5.getId());
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/liked")
                        .principal(() -> "person1@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(failCommentRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.likes").value(false));

        LikeRequest badTypeRequest = new LikeRequest();
        badTypeRequest.setType("Пост");
        badTypeRequest.setItemId(post1.getId());
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/liked")
                        .principal(() -> "person1@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(badTypeRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "person1@test.ru", authorities = "user:write")
    void getLikes() throws Exception {
        LikeRequest succsPostRequest = new LikeRequest();
        succsPostRequest.setType("Post");
        succsPostRequest.setItemId(post1.getId());
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/likes")
                        .principal(() -> "person1@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(succsPostRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.likes").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.users").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.users", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.users", containsInAnyOrder(
                        String.valueOf(person1.getId()), String.valueOf(person2.getId())))
                );

        LikeRequest succsCommentRequest = new LikeRequest();
        succsCommentRequest.setType("Comment");
        succsCommentRequest.setItemId(comment1.getId());
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/likes")
                        .principal(() -> "person1@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(succsCommentRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.likes").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.users").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.users", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.users", containsInAnyOrder(
                        String.valueOf(person1.getId()), String.valueOf(person2.getId())))
                );

        LikeRequest postNotFountRequest = new LikeRequest();
        postNotFountRequest.setType("Post");
        postNotFountRequest.setItemId(Long.MAX_VALUE);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/likes")
                        .principal(() -> "person1@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(postNotFountRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        LikeRequest commentNotFoundRequest = new LikeRequest();
        commentNotFoundRequest.setType("Comment");
        commentNotFoundRequest.setItemId(Long.MAX_VALUE);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/likes")
                        .principal(() -> "person1@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentNotFoundRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        LikeRequest postLikeNotFountRequest = new LikeRequest();
        postLikeNotFountRequest.setType("Post");
        postLikeNotFountRequest.setItemId(post3.getId());
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/likes")
                        .principal(() -> "person1@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(postLikeNotFountRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        LikeRequest commentLikeNotFountRequest = new LikeRequest();
        commentLikeNotFountRequest.setType("Post");
        commentLikeNotFountRequest.setItemId(comment5.getId());
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/likes")
                        .principal(() -> "person1@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentLikeNotFountRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        LikeRequest badTypeRequest = new LikeRequest();
        badTypeRequest.setType("Пост");
        badTypeRequest.setItemId(post1.getId());
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/likes")
                        .principal(() -> "person1@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(badTypeRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "person3@test.ru", authorities = "user:write")
    void putLike() throws Exception {
        LikeRequest postRequest = new LikeRequest();
        postRequest.setType("Post");
        postRequest.setItemId(post1.getId());
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/likes")
                        .principal(() -> "person3@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(postRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.likes").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.users").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.users", hasSize(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.users", containsInAnyOrder(
                        String.valueOf(person1.getId()), String.valueOf(person2.getId()), String.valueOf(person3.getId())))
                );

        LikeRequest commentRequest = new LikeRequest();
        commentRequest.setType("Comment");
        commentRequest.setItemId(comment1.getId());
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/likes")
                        .principal(() -> "person3@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.likes").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.users").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.users", hasSize(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.users", containsInAnyOrder(
                        String.valueOf(person1.getId()), String.valueOf(person2.getId()), String.valueOf(person3.getId())))
                );
    }

    @Test
    @WithMockUser(username = "person1@test.ru", authorities = "user:write")
    void deleteLike() throws Exception{
        LikeRequest succsPostRequest = new LikeRequest();
        succsPostRequest.setType("Post");
        succsPostRequest.setItemId(post1.getId());
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/likes")
                        .principal(() -> "person1@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(succsPostRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.likes").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.users").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.users", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.users", containsInAnyOrder(
                        String.valueOf(person2.getId())))
                );

        LikeRequest succsCommentRequest = new LikeRequest();
        succsCommentRequest.setType("Comment");
        succsCommentRequest.setItemId(comment1.getId());
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/likes")
                        .principal(() -> "person1@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(succsCommentRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.likes").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.users").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.users", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.users", containsInAnyOrder(
                       String.valueOf(person2.getId())))
                );

        LikeRequest badTypeRequest = new LikeRequest();
        badTypeRequest.setType("Пост");
        badTypeRequest.setItemId(post1.getId());
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/likes")
                        .principal(() -> "person1@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(badTypeRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        LikeRequest postLikeNotFountRequest = new LikeRequest();
        postLikeNotFountRequest.setType("Post");
        postLikeNotFountRequest.setItemId(Long.MAX_VALUE);
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/likes")
                        .principal(() -> "person1@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(postLikeNotFountRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        LikeRequest commentLikeNotFountRequest = new LikeRequest();
        commentLikeNotFountRequest.setType("Post");
        commentLikeNotFountRequest.setItemId(Long.MAX_VALUE);
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/likes")
                        .principal(() -> "person1@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentLikeNotFountRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}