package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.AbstractTest;
import com.skillbox.javapro21.domain.FriendshipStatus;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.domain.enumeration.FriendshipStatusType;
import com.skillbox.javapro21.domain.enumeration.MessagesPermission;
import com.skillbox.javapro21.repository.PersonRepository;
import com.skillbox.javapro21.service.impl.UtilsService;
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

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(value = {"classpath:application-test.properties"})
public class FriendsControllerTest extends AbstractTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private UtilsService utilsService;

    private Person verifyPersonSrc;
    private Person verifyPersonDst;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    @BeforeEach
    public void setup() {
        super.setup();
        String verifyEmail = "test99@test.ru";
        String password = "1234";
        String firstName = "Arcadiy";
        String lastName = "Parovozov";
        LocalDateTime reg_date = LocalDateTime.now();
        String conf_code = "123";

        verifyPersonSrc = new Person()
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
                .setLastOnlineTime(LocalDateTime.now())
                .setBirthDate(LocalDateTime.of(2000, 1, 1, 10, 0))
                .setCountry("Россия")
                .setTown("Москва");
        personRepository.save(verifyPersonSrc);

        verifyPersonDst = new Person()
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
        personRepository.save(verifyPersonDst);
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

        utilsService.createFriendship(verifyPersonDst, verifyPersonA, FriendshipStatusType.FRIEND);
    }

    @AfterEach
    public void cleanup() {
        personRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "test99@test.ru", authorities = "user:write")
    void getFriends() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/friends")
                        .principal(() -> "test99@test.ru")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.total").value(0)).andReturn();

        utilsService.createFriendship(verifyPersonSrc, verifyPersonDst, FriendshipStatusType.FRIEND);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/friends")
                        .principal(() -> "test99@test.ru")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.total").value(1)).andReturn();

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/friends")
                        .principal(() -> "test99@test.ru")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.total").value(1)).andReturn();
    }


    @Test
    @WithMockUser(username = "test99@test.ru", authorities = "user:write")
    void deleteFriend() throws Exception {
        utilsService.createFriendship(verifyPersonSrc, verifyPersonDst, FriendshipStatusType.FRIEND);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/friends/{id}", verifyPersonDst.getId())
                        .principal(() -> "test99@test.ru")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.message").value("ok")).andReturn();

        FriendshipStatus friendshipStatus = utilsService.getFriendshipStatus(verifyPersonDst.getId(), verifyPersonSrc.getId());
        Assertions.assertEquals(FriendshipStatusType.DECLINED, friendshipStatus.getFriendshipStatusType());
    }

    @Test
    @WithMockUser(username = "test99@test.ru", authorities = "user:write")
    void editFriendDeclined() throws Exception {
        utilsService.createFriendship(verifyPersonSrc, verifyPersonDst, FriendshipStatusType.DECLINED);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/friends/{id}", verifyPersonDst.getId())
                        .principal(() -> "test99@test.ru")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.message").value("ok")).andReturn();

        FriendshipStatus friendshipStatus = utilsService.getFriendshipStatus(verifyPersonDst.getId(), verifyPersonSrc.getId());
        Assertions.assertEquals(FriendshipStatusType.REQUEST, friendshipStatus.getFriendshipStatusType());
    }

    @Test
    @WithMockUser(username = "test99@test.ru", authorities = "user:write")
    void editNewFriend() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/friends/{id}", verifyPersonDst.getId())
                        .principal(() -> "test99@test.ru")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.message").value("ok")).andReturn();

        FriendshipStatus friendshipStatus = utilsService.getFriendshipStatus(verifyPersonDst.getId(), verifyPersonSrc.getId());
        Assertions.assertEquals(FriendshipStatusType.REQUEST, friendshipStatus.getFriendshipStatusType());
    }

    @Test
    @WithMockUser(username = "test99@test.ru", authorities = "user:write")
    void requestFriends() throws Exception {
        utilsService.createFriendship(verifyPersonSrc, verifyPersonDst, FriendshipStatusType.DECLINED);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/friends/request")
                        .principal(() -> "test99@test.ru")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.total").value(0)).andReturn();

        FriendshipStatus friendshipStatus = utilsService.getFriendshipStatus(verifyPersonSrc.getId(), verifyPersonDst.getId());
        Assertions.assertEquals(FriendshipStatusType.DECLINED, friendshipStatus.getFriendshipStatusType());

        utilsService.createFriendship(verifyPersonSrc, verifyPersonDst, FriendshipStatusType.REQUEST);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/friends/request")
                        .principal(() -> "test99@test.ru")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.total").value(1)).andReturn();
    }

    @Test
    @WithMockUser(username = "test99@test.ru", authorities = "user:write")
    void recommendationsFriends() throws Exception {
        utilsService.createFriendship(verifyPersonSrc, verifyPersonDst, FriendshipStatusType.FRIEND);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/friends/recommendations")
                        .principal(() -> "test99@test.ru")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.total").value(1)).andReturn();

        FriendshipStatus friendshipStatus = utilsService.getFriendshipStatus(verifyPersonSrc.getId(), verifyPersonDst.getId());
        Assertions.assertEquals(FriendshipStatusType.FRIEND, friendshipStatus.getFriendshipStatusType());

        utilsService.createFriendship(verifyPersonSrc, verifyPersonDst, FriendshipStatusType.DECLINED);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/friends/request")
                        .principal(() -> "test99@test.ru")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.total").value(0)).andReturn();
    }
}
