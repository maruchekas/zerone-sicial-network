package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.AbstractTest;
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

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(value = {"classpath:application.yml"})
//@TestPropertySource(value = {"classpath:application-test.properties"})
public class PostControllerTest extends AbstractTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private TagRepository tagRepository;

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

        Set<Tag> tags = new HashSet<>();
        tags.add(tag1);
        tags.add(tag2);

        Set<Tag> tag = new HashSet<>();
        tags.add(tag2);

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
        personRepository.delete(verifyPerson);
        personRepository.delete(verifyPersonWithPost);
        postRepository.delete(post1);
        postRepository.delete(post2);
        tagRepository.delete(tag1);
        tagRepository.delete(tag2);
    }

    @Test
    @WithMockUser(username = "test@test.ru", authorities = "user:write")
    void getPostsByAuthor() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/post")
                        .principal(() -> "test@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("text", "моржей")
                        .param("author", "Arcadiy-Канефоль")
                        .param("date_from", "1630091741000")
                        .param("date_to", "1640591802000"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.total").value(1));
    }

    @Test
    @WithMockUser(username = "test@test.ru", authorities = "user:write")
    void getPostsWithTag() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/post")
                        .principal(() -> "test@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("tag", "морскиеКотикиИзже")
                        .param("date_from", "1630091741000")
                        .param("date_to", "1640591802000"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.total").value(1));
    }

    @Test
    @WithMockUser(username = "test@test.ru", authorities = "user:write")
    void getPostsWithTags() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/post")
                        .principal(() -> "test@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("tag", "моржиНавсегда;морскиеКотикиИзже")
                        .param("date_from", "1630091741000")
                        .param("date_to", "1640591802000"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.total").value(1));
    }
}