package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.AbstractTest;
import com.skillbox.javapro21.api.request.post.CommentRequest;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.domain.Post;
import com.skillbox.javapro21.domain.PostComment;
import com.skillbox.javapro21.domain.Tag;
import com.skillbox.javapro21.domain.enumeration.MessagesPermission;
import com.skillbox.javapro21.repository.PersonRepository;
import com.skillbox.javapro21.repository.PostCommentRepository;
import com.skillbox.javapro21.repository.PostRepository;
import com.skillbox.javapro21.repository.TagRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(value = {"classpath:application-test.properties"})
public class PostCommentControllerTest extends AbstractTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private PostCommentRepository postCommentRepository;

    private Person verifyPerson;
    private Person verifyPersonWithPost;
    private Post post1;
    private Post post2;
    private Tag tag1;
    private Tag tag2;
    private PostComment postComment;
    private PostComment postCommentBlocked;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    @BeforeEach
    public void setup() {
        super.setup();
        String verifyEmail = "test@test.ru";
        String password = "1234";
        String firstName = "Arcadiy";
        String lastName = "Parovozov";
        LocalDateTime reg_date = LocalDateTime.now();
        String conf_code = "123";

        verifyPerson = new Person()
                .setEmail(verifyEmail)
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

        verifyPersonWithPost = new Person()
                .setEmail(verifyEmail + "b")
                .setPassword(passwordEncoder.encode(password))
                .setFirstName(firstName + "-Канефоль")
                .setLastName(lastName)
                .setConfirmationCode("123")
                .setRegDate(reg_date)
                .setConfirmationCode(conf_code)
                .setMessagesPermission(MessagesPermission.ALL)
                .setIsBlocked(0)
                .setIsApproved(1)
                .setLastOnlineTime(LocalDateTime.now().minusDays(2));

        personRepository.save(verifyPerson);
        personRepository.save(verifyPersonWithPost);

        tag1 = new Tag()
                .setTag("моржиНавсегда");
        tag2 = new Tag()
                .setTag("морскиеКотикиИзже");
        tagRepository.save(tag1);
        tagRepository.save(tag2);

        Set<Tag> tags = new HashSet<>();
        tags.add(tag1);
        tags.add(tag2);

        Set<Tag> tag = new HashSet<>();
        tag.add(tag2);

        post1 = new Post()
                .setTime(LocalDateTime.now().minusDays(1))
                .setAuthor(verifyPersonWithPost)
                .setTitle("Моржи")
                .setPostText("Лучше моржей только тюлени")
                .setIsBlocked(0)
                .setLikes(null)
                .setComments(null)
                .setTags(tag);
        postRepository.save(post1);

        post2 = new Post()
                .setTime(LocalDateTime.now().minusDays(1))
                .setAuthor(verifyPersonWithPost)
                .setTitle("Тюлени и моржи")
                .setPostText("I think about animals every day....")
                .setIsBlocked(0)
                .setLikes(null)
                .setComments(null)
                .setTags(tags);

        postRepository.save(post1);

        postComment = new PostComment()
                .setIsBlocked(0)
                .setCommentText("i love some ...")
                .setPerson(verifyPersonWithPost)
                .setPost(post2)
                .setTime(LocalDateTime.now().minusMinutes(2));

        postCommentBlocked = new PostComment()
                .setIsBlocked(2)
                .setCommentText("what do you want ...")
                .setPerson(verifyPersonWithPost)
                .setPost(post2)
                .setTime(LocalDateTime.now().minusMinutes(2));

        Set<PostComment> setComments = new HashSet<>();
        setComments.add(postComment);
        setComments.add(postCommentBlocked);

        post2.setComments(setComments);
        postRepository.save(post2);

        postCommentRepository.save(postComment);
        postCommentRepository.save(postCommentBlocked);
    }

    @AfterEach
    public void cleanup() {
        personRepository.deleteAll();
        postRepository.deleteAll();
        tagRepository.deleteAll();
        postCommentRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "test@test.ru", authorities = "user:write")
    void getComments() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/post/{id}/comments", post2.getId())
                        .principal(() -> "test@test.ru"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.total").value(1)).andReturn();

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/post/{id}/comments", 22)
                        .principal(() -> "test@test.ru"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @WithMockUser(username = "test@test.ru", authorities = "user:write")
    void postComments() throws Exception {
        CommentRequest commentRequest1 = new CommentRequest().setCommentText("lol");
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/post/{id}/comments", post2.getId())
                        .principal(() -> "test@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentRequest1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        CommentRequest commentRequest2 = new CommentRequest().setCommentText("lol").setParentId(1L);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/post/{id}/comments", 22)
                        .principal(() -> "test@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentRequest2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @WithMockUser(username = "test@test.ru", authorities = "user:write")
    void putCommentsNoAuthor() throws Exception {
        CommentRequest commentRequest1 = new CommentRequest().setCommentText("lol");
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/post/{id}/comments/{comment_id}", post2.getId(), postComment.getId())
                        .principal(() -> "test@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentRequest1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden()).andReturn();

        CommentRequest commentRequest2 = new CommentRequest().setCommentText("lol").setParentId(1L);
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/post/{id}/comments/{comment_id}", 22L, 1L)
                        .principal(() -> "test@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentRequest2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound()).andReturn();
    }

    @Test
    @WithMockUser(username = "test@test.rub", authorities = "user:write")
    void putCommentsWithAuthor() throws Exception {
        CommentRequest commentRequest1 = new CommentRequest().setCommentText("lol");
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/post/{id}/comments/{comment_id}", post2.getId(), postComment.getId())
                        .principal(() -> "test@test.rub")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentRequest1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        CommentRequest commentRequest2 = new CommentRequest().setCommentText("lol").setParentId(1L);
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/post/{id}/comments/{comment_id}", post2.getId(), postCommentBlocked.getId())
                        .principal(() -> "test@test.rub")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentRequest2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound()).andReturn();

        CommentRequest commentRequest3 = new CommentRequest().setCommentText("lol").setParentId(1L);
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/post/{id}/comments/{comment_id}", 22L, 1L)
                        .principal(() -> "test@test.rub")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentRequest3))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound()).andReturn();
    }


    @Test
    @WithMockUser(username = "test@test.rub", authorities = "user:write")
    void deleteCommentAuthor() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/post/{id}/comments/{comment_id}", post2.getId(), postComment.getId())
                        .principal(() -> "test@test.rub"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value( postComment.getId())).andReturn();

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/post/{id}/comments/{comment_id}", post2.getId(), postCommentBlocked.getId())
                        .principal(() -> "test@test.rub"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound()).andReturn();
    }

    @Test
    @WithMockUser(username = "test@test.ru", authorities = "user:write")
    void deleteCommentNoAuthor() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/post/{id}/comments/{comment_id}", post2.getId(), postComment.getId())
                        .principal(() -> "test@test.ru"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());

    }

    @Test
    @WithMockUser(username = "test@test.rub", authorities = "user:write")
    void recoveryCommentAuthor() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/post/{id}/comments/{comment_id}/recover", post2.getId(), postCommentBlocked.getId())
                        .principal(() -> "test@test.rub"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(postCommentBlocked.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.comment_text").value(postCommentBlocked.getCommentText())).andReturn();

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/post/{id}/comments/{comment_id}/recover", post2.getId(), postComment.getId())
                        .principal(() -> "test@test.rub"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound()).andReturn();

        Assertions.assertEquals(LocalDateTime.now().getDayOfMonth(), personRepository.findByEmail(verifyPersonWithPost.getEmail()).get().getLastOnlineTime().getDayOfMonth());
        System.out.println(personRepository.findByEmail(verifyPersonWithPost.getEmail()).get().getLastOnlineTime().getDayOfMonth());
    }

    @Test
    @WithMockUser(username = "test@test.ru", authorities = "user:write")
    void recoveryCommentNoAuthor() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/post/{id}/comments/{comment_id}/recover", post2.getId(), postCommentBlocked.getId())
                        .principal(() -> "test@test.ru"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden()).andReturn();

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/post/{id}/comments/{comment_id}/recover", post2.getId(), postComment.getId())
                        .principal(() -> "test@test.ru"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }


}
