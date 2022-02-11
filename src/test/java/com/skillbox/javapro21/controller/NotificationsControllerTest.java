package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.AbstractTest;
import com.skillbox.javapro21.api.request.notification.ReadNotificationRequest;
import com.skillbox.javapro21.api.response.notification.NotificationData;
import com.skillbox.javapro21.domain.*;
import com.skillbox.javapro21.domain.enumeration.MessagesPermission;
import com.skillbox.javapro21.repository.*;
import com.skillbox.javapro21.service.NotificationService;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.JsonPathResultMatchers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static com.skillbox.javapro21.domain.enumeration.FriendshipStatusType.FRIEND;
import static com.skillbox.javapro21.domain.enumeration.NotificationType.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(value = {"classpath:application-test.properties"})
class NotificationsControllerTest extends AbstractTest {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserNotificationSettingsRepository userNotificationSettingsRepository;
    @Autowired
    private FriendshipStatusRepository friendshipStatusRepository;
    @Autowired
    private FriendshipRepository friendshipRepository;
    @Autowired
    private NotificationService notificationService;

    private Notification notification1, notification2, notification3, notification4, notification5;

    @BeforeEach
    void setUp() {
        super.setup();

        Person person = new Person()
                .setEmail("test@test.ru")
                .setPassword(passwordEncoder.encode("123456"))
                .setFirstName("Test")
                .setLastName("Testovich")
                .setConfirmationCode("123")
                .setRegDate(LocalDateTime.now())
                .setMessagesPermission(MessagesPermission.ALL)
                .setIsBlocked(0)
                .setIsApproved(1)
                .setLastOnlineTime(LocalDateTime.now());
        Person friend = new Person()
                .setEmail("friend@test.ru")
                .setPassword(passwordEncoder.encode("123456"))
                .setFirstName("Friend")
                .setLastName("Friendovich")
                .setConfirmationCode("123")
                .setRegDate(LocalDateTime.now())
                .setBirthDate(LocalDateTime.now().minusYears(18))
                .setMessagesPermission(MessagesPermission.ALL)
                .setIsBlocked(0)
                .setIsApproved(1)
                .setLastOnlineTime(LocalDateTime.now());
        personRepository.saveAll(List.of(person, friend));

        FriendshipStatus status = new FriendshipStatus().setFriendshipStatusType(FRIEND).setTime(LocalDateTime.now()).setCode(11);
        friendshipStatusRepository.save(status);

        Friendship friendship = new Friendship()
                .setSrcPerson(person)
                .setDstPerson(friend)
                .setFriendshipStatus(status);
        friendshipRepository.save(friendship);

        UserNotificationSettings uns = new UserNotificationSettings()
                .setPerson(person)
                .setPostComment(true)
                .setCommentComment(true)
                .setFriendsRequest(true)
                .setMessage(true)
                .setFriendsBirthday(true);
        userNotificationSettingsRepository.save(uns);

        notification1 = new Notification()
                .setNotificationType(POST_COMMENT)
                .setSentTime(LocalDateTime.now())
                .setPerson(person)
                .setEntityId(1L)
                .setContact("Contact");
        notification2 = new Notification()
                .setNotificationType(COMMENT_COMMENT)
                .setSentTime(LocalDateTime.now())
                .setPerson(person)
                .setEntityId(2L)
                .setContact("Contact");
        notification3 = new Notification()
                .setNotificationType(FRIEND_REQUEST)
                .setSentTime(LocalDateTime.now())
                .setPerson(person)
                .setEntityId(3L)
                .setContact("Contact");
        notification4 = new Notification()
                .setNotificationType(MESSAGE)
                .setSentTime(LocalDateTime.now())
                .setPerson(person)
                .setEntityId(4L)
                .setContact("Contact");
        notification5 = new Notification()
                .setNotificationType(FRIEND_BIRTHDAY)
                .setSentTime(LocalDateTime.now())
                .setPerson(person)
                .setEntityId(5L)
                .setContact("Contact");

        notificationRepository.saveAll(List.of(notification1, notification2, notification3, notification4, notification5));
    }

    @AfterEach
    void tearDown() {
        personRepository.deleteAll();
        notificationRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "test@test.ru", authorities = "user:write")
    void getNotifications() throws Exception {
        mockMvc.perform(get("/api/v1/notifications")
                        .principal(() -> "test@test.ru"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(5)))
                .andExpect(jsonPath("$.data[*].id", containsInAnyOrder(
                                notification1.getId().intValue(), notification2.getId().intValue(),
                                notification3.getId().intValue(), notification4.getId().intValue(),
                                notificationRepository.findAllByNotificationTypeEquals(FRIEND_BIRTHDAY).get(0).getId().intValue())));

    }


    @Test
    @WithMockUser(username = "test@test.ru", authorities = "user:write")
    void readNotification() throws Exception {
        ReadNotificationRequest request = new ReadNotificationRequest();
        request.setId(notification2.getId());

        mockMvc.perform(put("/api/v1/notifications")
                        .principal(() -> "test@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(4)))
                .andExpect(jsonPath("$.data[*].id", containsInAnyOrder(
                        notification1.getId().intValue(),
                        notification3.getId().intValue(), notification4.getId().intValue(),
                        notificationRepository.findAllByNotificationTypeEquals(FRIEND_BIRTHDAY).get(0).getId().intValue())));

        request.setAll(true);

        mockMvc.perform(put("/api/v1/notifications")
                        .principal(() -> "test@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(0)));
    }
}