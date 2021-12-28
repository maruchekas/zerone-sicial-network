package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.AbstractTest;
import com.skillbox.javapro21.api.request.post.PostRequest;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.domain.Post;
import com.skillbox.javapro21.domain.Tag;
import com.skillbox.javapro21.domain.enumeration.MessagesPermission;
import com.skillbox.javapro21.repository.PersonRepository;
import com.skillbox.javapro21.repository.PostRepository;
import com.skillbox.javapro21.repository.TagRepository;
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

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
//@TestPropertySource(value = {"classpath:application.yml"})
@TestPropertySource(value = {"classpath:application-test.properties"})
public class PostControllerTest extends AbstractTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private TagRepository tagRepository;

    String now = String.valueOf(Instant.now().getEpochSecond() * 1000);
    String yearAgo = String.valueOf(Instant.now().minusSeconds(31536000).getEpochSecond() * 1000);

    private Person verifyPerson;
    private Person verifyPersonWithPost;
    private Post post1;
    private Post post2;
    private Tag tag1;
    private Tag tag2;

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
                .setLastOnlineTime(LocalDateTime.now());

        personRepository.save(verifyPerson);
        personRepository.save(verifyPersonWithPost);

        tag1 = new Tag()
                .setTag("моржиНавсегда");
        tag2 = new Tag()
                .setTag("морскиеКотикиИзже");
        tagRepository.save(tag1);
        tagRepository.save(tag2);

        List<Tag> tags = new ArrayList<>();
        tags.add(tag1);
        tags.add(tag2);

        List<Tag> tag = new ArrayList<>();
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
        postRepository.save(post2);
    }

    @AfterEach
    public void cleanup() {
        personRepository.deleteAll();
        postRepository.deleteAll();
        tagRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "test@test.ru", authorities = "user:write")
    void getPostsByAuthor() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/post")
                        .principal(() -> "test@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("author", "Arcadiy-Канефоль")
                        .param("date_from", yearAgo)
                        .param("date_to", now))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.total").value(2));
    }

    @Test
    @WithMockUser(username = "test@test.ru", authorities = "user:write")
    void getPostsWithTag() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/post")
                        .principal(() -> "test@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("tag", "морскиеКотикиИзже")
                        .param("date_from", yearAgo)
                        .param("date_to", now))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.total").value(2));
    }

    @Test
    @WithMockUser(username = "test@test.ru", authorities = "user:write")
    void getPostsWithTags() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/post")
                        .principal(() -> "test@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("tag", "моржиНавсегда;морскиеКотикиИзже")
                        .param("date_from", yearAgo)
                        .param("date_to", now))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.total").value(2));
    }

    @Test
    @WithMockUser(username = "test@test.ru", authorities = "user:write")
    void getPostsByIdFor404() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/post/{id}", 44)
                        .principal(() -> "test@test.ru")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "test@test.ru", authorities = "user:write")
    void getPostsById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/post/{id}", post1.getId())
                        .principal(() -> "test@test.ru")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(post1.getId()));
    }

    @Test
    void getPostsByIdWithoutAuthorization() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/post/{id}", post1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(username = "test@test.rub", authorities = "user:write")
    void putPostByIdAndMessageInDay() throws Exception {
        PostRequest postRequest = new PostRequest()
                .setPostText("how much u want...")
                .setTitle("wtf");

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/post/{id}", post1.getId())
                        .principal(() -> "test@test.rub")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(postRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(post1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.post_text").value(postRequest.getPostText()));
    }
    @Test
    @WithMockUser(username = "test@test.ru", authorities = "user:write")
    void putPostByIdAndMessageInDayForBadRequest() throws Exception {
        PostRequest postRequest = new PostRequest()
                .setPostText("how much u want...")
                .setTitle("wtf");

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/post/{id}", post1.getId())
                        .principal(() -> "test@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(postRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}