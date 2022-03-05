package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.AbstractTest;
import com.skillbox.javapro21.api.request.post.PostRequest;
import com.skillbox.javapro21.api.request.profile.EditProfileRequest;
import com.skillbox.javapro21.domain.*;
import com.skillbox.javapro21.domain.enumeration.FriendshipStatusType;
import com.skillbox.javapro21.domain.enumeration.MessagesPermission;
import com.skillbox.javapro21.repository.*;
import com.skillbox.javapro21.service.ProfileService;
import com.skillbox.javapro21.service.impl.UtilsService;
import com.sun.security.auth.UserPrincipal;
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

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.skillbox.javapro21.config.Constants.USER_WASBLOCKED_ERR;
import static com.skillbox.javapro21.domain.enumeration.FriendshipStatusType.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(value = {"classpath:application-test.properties"})
public class ProfileControllerTest extends AbstractTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private FriendshipRepository friendshipRepository;
    @Autowired
    private FriendshipStatusRepository friendshipStatusRepository;
    @Autowired
    private UtilsService utilsService;
    @Autowired
    private ProfileService profileService;

    private Person verifyPerson;
    private Person verifyPersonWithPost;

    private Post post1;
    private Post post2;
    private Tag tag1;
    private Tag tag2;

    private Friendship friendshipSrc;
    private Friendship friendshipDst;
    private FriendshipStatus friendshipStatusSrc;
    private FriendshipStatus friendshipStatusDst;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    @BeforeEach
    public void setup() {
        super.setup();
        String verifyEmail = "test1@test.ru";
        String password = "1234";
        String firstName = "Arcadiy";
        String lastName = "Parovozov";
        LocalDateTime birthDate = LocalDateTime.now().minusYears(20);
        LocalDateTime reg_date = LocalDateTime.now();
        String conf_code = "123";

        verifyPerson = new Person()
                .setEmail(verifyEmail)
                .setPassword(passwordEncoder.encode(password))
                .setFirstName(firstName)
                .setLastName(lastName)
                .setBirthDate(birthDate)
                .setConfirmationCode("123")
                .setRegDate(reg_date)
                .setConfirmationCode(conf_code)
                .setMessagesPermission(MessagesPermission.ALL)
                .setIsBlocked(0)
                .setIsApproved(1)
                .setLastOnlineTime(LocalDateTime.now())
                .setBirthDate(LocalDateTime.of(2000, 1, 1, 10, 0))
                .setCountry("Россия")
                .setTown("Москва");
        personRepository.save(verifyPerson);

        Person verifyPersonA = new Person()
                .setEmail(verifyEmail + "A")
                .setPassword(passwordEncoder.encode(password))
                .setFirstName(firstName + "-Альфа")
                .setLastName(lastName)
                .setConfirmationCode("123")
                .setRegDate(reg_date)
                .setConfirmationCode(conf_code)
                .setMessagesPermission(MessagesPermission.ALL)
                .setIsBlocked(0)
                .setIsApproved(1)
                .setLastOnlineTime(LocalDateTime.now())
                .setBirthDate(LocalDateTime.of(2000, 1, 1, 10, 0))
                .setCountry("Россия")
                .setTown("Москва");
        personRepository.save(verifyPersonA);

        Person verifyPersonB = new Person()
                .setEmail(verifyEmail + "B")
                .setPassword(passwordEncoder.encode(password))
                .setFirstName(firstName + "-Бетта")
                .setLastName(lastName)
                .setConfirmationCode("123")
                .setRegDate(reg_date)
                .setConfirmationCode(conf_code)
                .setMessagesPermission(MessagesPermission.ALL)
                .setIsBlocked(1)
                .setIsApproved(1)
                .setLastOnlineTime(LocalDateTime.now())
                .setBirthDate(LocalDateTime.of(2000, 1, 1, 10, 0))
                .setCountry("Россия")
                .setTown("Москва");
        personRepository.save(verifyPersonB);

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
                .setLastOnlineTime(LocalDateTime.now().minusDays(2))
                .setBirthDate(LocalDateTime.of(2010, 1, 1, 10, 0))
                .setCountry("Россия")
                .setTown("Санкт-Петербург");
        personRepository.save(verifyPersonWithPost);

        friendshipStatusSrc = new FriendshipStatus();
        friendshipStatusSrc.setFriendshipStatusType(FriendshipStatusType.FRIEND);
        friendshipStatusSrc.setTime(LocalDateTime.now().minusDays(1));
        friendshipStatusRepository.save(friendshipStatusSrc);

        friendshipStatusDst = new FriendshipStatus();
        friendshipStatusDst.setFriendshipStatusType(FriendshipStatusType.FRIEND);
        friendshipStatusDst.setTime(LocalDateTime.now().minusDays(1));
        friendshipStatusRepository.save(friendshipStatusDst);

        FriendshipStatus statusA = new FriendshipStatus();
        statusA.setFriendshipStatusType(FRIEND);
        statusA.setTime(LocalDateTime.now().minusDays(1));
        friendshipStatusRepository.save(statusA);

        FriendshipStatus statusB = new FriendshipStatus();
        statusB.setFriendshipStatusType(BLOCKED);
        statusB.setTime(LocalDateTime.now().minusDays(1));
        friendshipStatusRepository.save(statusB);

        friendshipSrc = new Friendship();
        friendshipSrc.setSrcPerson(verifyPerson);
        friendshipSrc.setDstPerson(verifyPersonWithPost);
        friendshipSrc.setFriendshipStatus(friendshipStatusSrc);
        friendshipRepository.save(friendshipSrc);

        friendshipDst = new Friendship();
        friendshipDst.setSrcPerson(verifyPersonWithPost);
        friendshipDst.setDstPerson(verifyPerson);
        friendshipDst.setFriendshipStatus(friendshipStatusDst);
        friendshipRepository.save(friendshipDst);

        Friendship friendshipA = new Friendship();
        friendshipA.setSrcPerson(verifyPerson);
        friendshipA.setDstPerson(verifyPersonA);
        friendshipA.setFriendshipStatus(statusA);
        friendshipRepository.save(friendshipA);

        Friendship friendshipB = new Friendship();
        friendshipB.setSrcPerson(verifyPerson);
        friendshipB.setDstPerson(verifyPersonB);
        friendshipB.setFriendshipStatus(statusB);
        friendshipRepository.save(friendshipB);

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
        postRepository.save(post2);
    }

    @AfterEach
    public void cleanup() {
        personRepository.deleteAll();
        postRepository.deleteAll();
        friendshipRepository.deleteAll();
        friendshipStatusRepository.deleteAll();
        tagRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "test1@test.ru", authorities = "user:write")
    void deletePerson() throws Exception {
        Assertions.assertEquals(verifyPerson.getEmail(), "test1@test.ru");

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/users/me")
                        .principal(() -> "test1@test.ru"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        Assertions.assertNotEquals("Пользователь не удален", verifyPerson.getEmail(), "test@test.ru");
    }

    @Test
    @WithMockUser(username = "test1@test.ru", authorities = "user:write")
    void getPerson() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/users/me")
                .principal(() -> "test1@test.ru"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("string"));
    }

    @Test
    @WithMockUser(username = "test1@test.ru", authorities = "user:write")
    void editPerson() throws Exception {
        Optional<Person> byEmail = personRepository.findByEmail("test1@test.ru");
        EditProfileRequest editProfileRequest = new EditProfileRequest();
        editProfileRequest.setFirstName("Oleg");
        editProfileRequest.setBirthDate(byEmail.get().getBirthDate().toInstant(ZoneOffset.UTC).toEpochMilli());

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/users/me")
                        .principal(() -> "test1@test.ru")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(editProfileRequest))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.first_name").value("Oleg"));
    }

    @Test
    @WithMockUser(username = "test1@test.ru", authorities = "user:write")
    void getPersonById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/users/{id}", verifyPerson.getId())
                        .principal(() -> "test1@test.ru")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.first_name").value("Arcadiy"));
    }

    @Test
    @WithMockUser(username = "test1@test.ru", authorities = "user:write")
    void getPersonWallById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/users/{id}/wall", verifyPersonWithPost.getId())
                        .principal(() -> "test1@test.ru")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.total").value(2));
    }

    @Test
    @WithMockUser(username = "test1@test.ru", authorities = "user:write")
    void getPersonWallByIdButOneIsBlocked() throws Exception {
        friendshipStatusSrc.setFriendshipStatusType(BLOCKED);
        friendshipStatusSrc.setTime(LocalDateTime.now().minusHours(2));
        friendshipStatusRepository.save(friendshipStatusSrc);
        friendshipSrc.setFriendshipStatus(friendshipStatusSrc);
        friendshipRepository.save(friendshipSrc);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/users/{id}/wall", verifyPersonWithPost.getId())
                        .principal(() -> "test1@test.ru")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "test1@test.ru", authorities = "user:write")
    void getPersonWallByIdWhenPersonIsBlocker() throws Exception {
        friendshipStatusSrc.setFriendshipStatusType(WASBLOCKED);
        friendshipStatusSrc.setTime(LocalDateTime.now().minusHours(2));
        friendshipStatusRepository.save(friendshipStatusSrc);
        friendshipSrc.setFriendshipStatus(friendshipStatusSrc);
        friendshipRepository.save(friendshipSrc);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/users/{id}/wall", verifyPersonWithPost.getId())
                        .principal(() -> "test1@test.ru")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        friendshipStatusSrc.setFriendshipStatusType(INTERLOCKED);
        friendshipStatusSrc.setTime(LocalDateTime.now().minusHours(2));
        friendshipStatusRepository.save(friendshipStatusSrc);
        friendshipSrc.setFriendshipStatus(friendshipStatusSrc);
        friendshipRepository.save(friendshipSrc);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/users/{id}/wall", verifyPersonWithPost.getId())
                        .principal(() -> "test1@test.ru")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("user"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_description").value(USER_WASBLOCKED_ERR));
    }


    @Test
    @WithMockUser(username = "test1@test.ru", authorities = "user:write")
    void postPostOnPersonWallById() throws Exception {
        PostRequest postRequest = new PostRequest()
                .setPostText("Mu-Mu")
                .setTags(new String[0])
                .setTitle("Pushkin?");

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/users/{id}/wall", verifyPersonWithPost.getId())
                        .principal(() -> "test1@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(postRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.title").value("Pushkin?"));
    }

    @Test
    @WithMockUser(username = "test1@test.ru", authorities = "user:write")
    void postPostOnPersonWallByIdIsBlocked() throws Exception {
        friendshipStatusSrc.setFriendshipStatusType(BLOCKED);
        friendshipStatusSrc.setTime(LocalDateTime.now().minusHours(2));
        friendshipStatusRepository.save(friendshipStatusSrc);
        friendshipSrc.setFriendshipStatus(friendshipStatusSrc);
        friendshipRepository.save(friendshipSrc);
        PostRequest postRequest = new PostRequest()
                .setPostText("Mu-Mu")
                .setTags(new String[0])
                .setTitle("Pushkin?");

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/users/{id}/wall", verifyPersonWithPost.getId())
                        .principal(() -> "test1@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(postRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test1@test.ru", authorities = "user:write")
    void blockPersonById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/users/block/{id}", verifyPersonWithPost.getId())
                        .principal(() -> "test1@test.ru")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.message").value("ok"));
        Assertions.assertEquals(BLOCKED, utilsService.getFriendshipStatus(verifyPerson.getId(), verifyPersonWithPost.getId()).getFriendshipStatusType());
        Assertions.assertEquals(WASBLOCKED, utilsService.getFriendshipStatus(verifyPersonWithPost.getId(), verifyPerson.getId()).getFriendshipStatusType());
    }

    @Test
    @WithMockUser(username = "test1@test.ru", authorities = "user:write")
    void unblockPersonById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/users/block/{id}", verifyPersonWithPost.getId())
                        .principal(() -> "test1@test.ru")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.message").value("ok")).andReturn();
        Assertions.assertEquals(BLOCKED, utilsService.getFriendshipStatus(verifyPerson.getId(), verifyPersonWithPost.getId()).getFriendshipStatusType());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/users/block/{id}", verifyPersonWithPost.getId())
                        .principal(() -> "test1@test.ru")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.message").value("ok"));

        Assertions.assertNull(utilsService.getFriendshipStatus(verifyPersonWithPost.getId(), verifyPerson.getId()));
    }

    @Test
    @WithMockUser(username = "test1@test.ru", authorities = "user:write")
    void searchByPerson() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/users/search", "Arcadiy")
                        .principal(() -> "test1@test.ru")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
        Principal principal = new UserPrincipal("test1@test.ru");
        Assertions.assertEquals(2, profileService.searchByPerson("", "", 0, 150, "", "", 0, 20, principal).getTotal());
        Assertions.assertEquals(2, profileService.searchByPerson("Arcadiy", "", 0, 150, "", "", 0, 20, principal).getTotal());
        Assertions.assertEquals(2, profileService.searchByPerson("", "ovoz", 0, 150, "", "", 0, 20, principal).getTotal());
        Assertions.assertEquals(1, profileService.searchByPerson("diy", "ovoz", 18, 30, "", "", 0, 20, principal).getTotal());
        Assertions.assertEquals(2, profileService.searchByPerson("diy", "ovoz", 0, 30, "", "", 0, 20, principal).getTotal());
        Assertions.assertEquals(2, profileService.searchByPerson("diy", "ovoz", 0, 30, "Россия", "", 0, 20, principal).getTotal());
        Assertions.assertEquals(1, profileService.searchByPerson("diy", "ovoz", 0, 30, "Россия", "Москва", 0, 20, principal).getTotal());
        Assertions.assertEquals(1, profileService.searchByPerson("Фксфвшн", "", 0, 30, "Россия", "Москва", 0, 20, principal).getTotal());
        Assertions.assertEquals(1, profileService.searchByPerson("", "Зфкщмщя", 0, 30, "Россия", "Москва", 0, 20, principal).getTotal());
    }


}
